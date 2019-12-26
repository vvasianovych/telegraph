package com.keebraa.telegraph.remote;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keebraa.telegraph.annotations.RemoteService;
import com.keebraa.telegraph.exception.CommunicationException;
import com.keebraa.telegraph.lib.MethodArg;
import com.keebraa.telegraph.lib.MethodCall;
import com.keebraa.telegraph.lib.MethodCallResult;
import com.keebraa.telegraph.lib.MicroserviceDescriptor;
import com.keebraa.telegraph.remote.defaultmethods.BasicMethodHandler;
import com.keebraa.telegraph.remote.defaultmethods.EqualsMethodHandler;
import com.keebraa.telegraph.remote.defaultmethods.HashCodeMethodHandler;
import com.keebraa.telegraph.remote.defaultmethods.ToStringMethodHandler;

public class ProxyMethodInvocationHandler implements InvocationHandler {

    private static Logger log = LoggerFactory.getLogger("Telegraph");

    private static final Map<String, BasicMethodHandler> basicMethods = new HashMap<>();

    static {
        ToStringMethodHandler toString = new ToStringMethodHandler();
        basicMethods.put(toString.getMethodName(), toString);

        HashCodeMethodHandler hashCode = new HashCodeMethodHandler();
        basicMethods.put(hashCode.getMethodName(), hashCode);

        EqualsMethodHandler equals = new EqualsMethodHandler();
        basicMethods.put(equals.getMethodName(), equals);
    }

    private final Class<?> remoteInterface;

    private final RemoteServiceResolver resolver;

    private final RemoteSocketRegistry socketRegistry;

    private final ObjectMapper mapper;

    private int attempts;

    private long delay;

    public ProxyMethodInvocationHandler(Class<?> remoteInterface, ObjectMapper objectMapper, RemoteServiceResolver resolver,
            RemoteSocketRegistry socketRegistry, int attemptsCount, long msResolvingDelay) {
        this.remoteInterface = remoteInterface;
        this.socketRegistry = socketRegistry;
        this.resolver = resolver;
        this.attempts = attemptsCount;
        this.delay = msResolvingDelay;
        this.mapper = objectMapper;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        BasicMethodHandler basicMethodHandler = basicMethods.get(method.getName());

        if (basicMethodHandler != null) {
            return basicMethodHandler.invoke(remoteInterface, args);
        }

        String methodName = method.getName();
        String serviceName = resolveServiceName(remoteInterface);
        String msName = resolveMicroserviceName(remoteInterface);

        List<MethodArg> methodArgs = buildArgs(method, args);

        MethodCall methodCall = buildMethodCall(msName, serviceName, methodName, methodArgs);
        MicroserviceDescriptor msDescriptor = resolveDescriptor(msName);
        MethodCallResult methodCallResult = null;

        try {
            methodCallResult = callMethod(msDescriptor, methodCall);
            methodCallResult.setCall(methodCall);
        } catch (IOException e) {
            throw new CommunicationException(msName, e.getMessage());
        }

        if (method.getReturnType().isAssignableFrom(MethodCallResult.class))
            return methodCallResult;

        Class<?> returnClass = this.getClass().getClassLoader().loadClass(methodCallResult.getReturnType());
        return mapper.convertValue(methodCallResult.getResult(), returnClass);
    }

    private List<MethodArg> buildArgs(Method method, Object... args) {
        List<MethodArg> result = new ArrayList<>();
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter param = method.getParameters()[i];
            MethodArg arg = new MethodArg();
            arg.setName(param.getName());
            arg.setType(param.getType().getName());
            if (args.length > i) {
                arg.setValue(args[i]);
            }
            result.add(arg);
        }
        return result;
    }

    private MethodCallResult callMethod(MicroserviceDescriptor descriptor, MethodCall methodCall) throws IOException {
        String payload = mapper.writeValueAsString(methodCall);
        MethodCallResult callResult = null;
        int attempt = 0;
        do {
            try (Socket socket = socketRegistry.resolveSocket(descriptor)) {
                if (socket == null)
                    return null;
                synchronized (socket) {
                    BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
                    BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    outputStream.write(payload.getBytes());
                    outputStream.flush();
                    String result = inputStreamReader.readLine();
                    // If this is last attempt and result is null - raise the exception
                    if (isBlank(result)) {
                        if (attempt >= (attempts - 1)) {
                            throw new IOException("empty response from remote service");
                        }
                    } else {
                        try {
                            callResult = mapper.readValue(result, MethodCallResult.class);
                        } catch (JsonMappingException e) {
                            log.debug("Failed during parse the answer. details: {}, Response: {}", e.getMessage(), result);
                            if (attempt >= (attempts - 1)) {
                                throw new IOException("invalid response from remote service", e);
                            }
                        }
                    }
                }
            }
            attempt++;
        } while (callResult == null && attempt < attempts);
        if (callResult == null)

        {
            throw new IOException("empty response from remote service");
        }
        return callResult;
    }

    private MicroserviceDescriptor resolveDescriptor(String msName) throws IOException, InterruptedException {
        MicroserviceDescriptor msDescriptor = null;
        int attempt = 0;
        do {
            msDescriptor = resolver.resolveService(msName);
            if (msDescriptor == null) {
                log.debug("Microservice is not resolved. name: {}. attempt: {} of {}. delay: {}", msName, attempt, attempts, delay);
                Thread.sleep(delay);
            }
            attempt++;
        } while (msDescriptor == null && attempt <= attempts);
        String host = msDescriptor != null ? msDescriptor.getHost() : null;
        log.debug("Microservice resolving result: {}. Host:{}", msDescriptor != null, host);
        return msDescriptor;
    }

    private MethodCall buildMethodCall(String msName, String serviceName, String methodName, List<MethodArg> args) {
        MethodCall call = new MethodCall();
        call.setMicroserviceName(msName);
        call.setServiceName(serviceName);
        call.setMethodName(methodName);
        call.setArgs(args);
        return call;
    }

    private String resolveMicroserviceName(Class<?> remoteInterface) {
        RemoteService remoteService = remoteInterface.getAnnotation(RemoteService.class);
        return remoteService.msName();
    }

    private String resolveServiceName(Class<?> remoteInterface) {
        RemoteService remoteService = remoteInterface.getAnnotation(RemoteService.class);
        String serviceName = remoteService.serviceName();
        if (StringUtils.isBlank(serviceName)) {
            serviceName = remoteInterface.getSimpleName();
        }
        return serviceName;
    }
}
