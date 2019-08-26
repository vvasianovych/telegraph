package com.keebraa.telegraph.remote;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keebraa.telegraph.remote.defaultmethods.BasicMethodHandler;
import com.keebraa.telegraph.remote.defaultmethods.EqualsMethodHandler;
import com.keebraa.telegraph.remote.defaultmethods.HashCodeMethodHandler;
import com.keebraa.telegraph.remote.defaultmethods.ToStringMethodHandler;

public class ProxyMethodInvokationHandler implements InvocationHandler {

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

    private final String name;

    private final String remoteHost;

    private Socket remoteServiceSocket = null;

    private int port;

    public ProxyMethodInvokationHandler(Class<?> remoteInterface, ObjectMapper objectMapper, String name, String remoteHost, int port) {
        this.remoteHost = remoteHost;
        this.remoteInterface = remoteInterface;
        this.name = name;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        BasicMethodHandler basicMethodHandler = basicMethods.get(method.getName());
        if (basicMethodHandler != null) {
            return basicMethodHandler.invoke(remoteInterface, name, remoteHost, args);
        }
        String methodName = method.getName();
        return null;
    }

    private Socket getSocket() throws UnknownHostException, IOException {
        if (remoteServiceSocket == null) {
            remoteServiceSocket = new Socket(remoteHost, port);
        }
        // Check the socket
        remoteServiceSocket.getInputStream();
        remoteServiceSocket.getOutputStream();
        return remoteServiceSocket;
    }
}
