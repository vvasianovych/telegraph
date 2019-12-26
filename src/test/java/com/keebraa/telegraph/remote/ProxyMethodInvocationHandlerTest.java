package com.keebraa.telegraph.remote;

import static com.keebraa.telegraph.lib.CallStatus.SUCCESS;
import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keebraa.telegraph.exception.CommunicationException;
import com.keebraa.telegraph.lib.MethodArg;
import com.keebraa.telegraph.lib.MethodCallResult;
import com.keebraa.telegraph.lib.MicroserviceDescriptor;
import com.keebraa.telegraph.lib.SharedServiceDescriptor;
import com.keebraa.telegraph.lib.SharedServiceMethodDescriptor;
import com.keebraa.telegraph.remote.testdata.RemoteTestService;
import com.keebraa.telegraph.remote.testdata.TestDomainObject;

public class ProxyMethodInvocationHandlerTest {

    @Test
    public void testCall_normalCase() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        TestDomainObject remoteCallResult = new TestDomainObject();
        remoteCallResult.setId("123");
        remoteCallResult.setProperties(singletonMap("something", "very important"));

        MethodCallResult methodCallResult = new MethodCallResult();
        methodCallResult.setResult(remoteCallResult);
        methodCallResult.setReturnType(TestDomainObject.class.getName());
        methodCallResult.setStatus(SUCCESS);

        RemoteServiceResolver mockedResolver = mock(RemoteServiceResolver.class);
        RemoteSocketRegistry mockedSocketRegistry = mock(RemoteSocketRegistry.class);
        Socket mockedSocket = mock(Socket.class);
        OutputStream mockedOutputStream = mock(OutputStream.class);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mapper.writeValueAsBytes(methodCallResult));
        byteArrayInputStream.reset();
        ProxyMethodInvocationHandler handler = new ProxyMethodInvocationHandler(RemoteTestService.class, mapper, mockedResolver, mockedSocketRegistry, 5, 1000);

        RemoteTestService proxiedService = (RemoteTestService) newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { RemoteTestService.class },
                handler);

        MicroserviceDescriptor descriptor = new MicroserviceDescriptor();
        descriptor.setName("testMS");
        descriptor.setHost("localhost");
        descriptor.setPort(9999);

        SharedServiceDescriptor sharedServiceDescriptor = new SharedServiceDescriptor();
        sharedServiceDescriptor.setServiceClassname(RemoteTestService.class.getName());
        sharedServiceDescriptor.setServiceName(RemoteTestService.class.getSimpleName());

        SharedServiceMethodDescriptor methodDescriptor = new SharedServiceMethodDescriptor();
        methodDescriptor.setName("getTestDomainObjectById");
        methodDescriptor.setReturnTypeClassName("java.util.Map");

        MethodArg arg = new MethodArg();
        arg.setName("id");
        arg.setType("java.lang.String");

        methodDescriptor.setArguments(asList(arg));

        Map<String, SharedServiceMethodDescriptor> methodDescriptors = new HashMap<>();
        methodDescriptors.put("getTestDomainObjectById", methodDescriptor);

        sharedServiceDescriptor.setMethods(methodDescriptors);

        descriptor.setServiceDescriptors(asList(sharedServiceDescriptor));

        when(mockedResolver.resolveService("testMS")).thenReturn(descriptor);
        when(mockedSocketRegistry.resolveSocket(descriptor)).thenReturn(mockedSocket);
        when(mockedSocket.getInputStream()).thenReturn(byteArrayInputStream);
        when(mockedSocket.getOutputStream()).thenReturn(mockedOutputStream);

        TestDomainObject callResultObject = proxiedService.getTestDomainObjectById("123");

        assertThat(callResultObject.getId(), equalTo(remoteCallResult.getId()));
        assertTrue(callResultObject.getProperties().equals(remoteCallResult.getProperties()));
    }
    
    @Test
    public void testCall_invalidInputStreamCase() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        TestDomainObject remoteCallResult = new TestDomainObject();
        remoteCallResult.setId("123");
        remoteCallResult.setProperties(singletonMap("something", "very important"));

        MethodCallResult methodCallResult = new MethodCallResult();
        methodCallResult.setResult(remoteCallResult);
        methodCallResult.setReturnType(TestDomainObject.class.getName());
        methodCallResult.setStatus(SUCCESS);

        RemoteServiceResolver mockedResolver = mock(RemoteServiceResolver.class);
        RemoteSocketRegistry mockedSocketRegistry = mock(RemoteSocketRegistry.class);
        Socket mockedSocket = mock(Socket.class);
        OutputStream mockedOutputStream = mock(OutputStream.class);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[0]);

        ProxyMethodInvocationHandler handler = new ProxyMethodInvocationHandler(RemoteTestService.class, mapper, mockedResolver, mockedSocketRegistry, 5, 1000);

        RemoteTestService proxiedService = (RemoteTestService) newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { RemoteTestService.class },
                handler);

        MicroserviceDescriptor descriptor = new MicroserviceDescriptor();
        descriptor.setName("testMS");
        descriptor.setHost("localhost");
        descriptor.setPort(9999);

        SharedServiceDescriptor sharedServiceDescriptor = new SharedServiceDescriptor();
        sharedServiceDescriptor.setServiceClassname(RemoteTestService.class.getName());
        sharedServiceDescriptor.setServiceName(RemoteTestService.class.getSimpleName());

        SharedServiceMethodDescriptor methodDescriptor = new SharedServiceMethodDescriptor();
        methodDescriptor.setName("getTestDomainObjectById");
        methodDescriptor.setReturnTypeClassName("java.util.Map");

        MethodArg arg = new MethodArg();
        arg.setName("id");
        arg.setType("java.lang.String");

        methodDescriptor.setArguments(asList(arg));

        Map<String, SharedServiceMethodDescriptor> methodDescriptors = new HashMap<>();
        methodDescriptors.put("getTestDomainObjectById", methodDescriptor);

        sharedServiceDescriptor.setMethods(methodDescriptors);

        descriptor.setServiceDescriptors(asList(sharedServiceDescriptor));

        when(mockedResolver.resolveService("testMS")).thenReturn(descriptor);
        when(mockedSocketRegistry.resolveSocket(descriptor)).thenReturn(mockedSocket);
        when(mockedSocket.getInputStream()).thenReturn(byteArrayInputStream);
        when(mockedSocket.getOutputStream()).thenReturn(mockedOutputStream);

        try {
            proxiedService.getTestDomainObjectById("123");
        } catch (CommunicationException e) {
            assertThat(e.getMessage(), containsString("empty response from remote service"));
        }
    }
    
    @Test
    public void testCall_invalidResponseCase() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        TestDomainObject remoteCallResult = new TestDomainObject();
        remoteCallResult.setId("123");
        remoteCallResult.setProperties(singletonMap("something", "very important"));

        MethodCallResult methodCallResult = new MethodCallResult();
        methodCallResult.setResult(remoteCallResult);
        methodCallResult.setReturnType(TestDomainObject.class.getName());
        methodCallResult.setStatus(SUCCESS);

        RemoteServiceResolver mockedResolver = mock(RemoteServiceResolver.class);
        RemoteSocketRegistry mockedSocketRegistry = mock(RemoteSocketRegistry.class);
        Socket mockedSocket = mock(Socket.class);
        OutputStream mockedOutputStream = mock(OutputStream.class);
        
        String responseString = mapper.writeValueAsString(methodCallResult);
        // Make response invalid
        responseString = responseString.substring(0, 100);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(responseString.getBytes());

        ProxyMethodInvocationHandler handler = new ProxyMethodInvocationHandler(RemoteTestService.class, mapper, mockedResolver, mockedSocketRegistry, 5, 1000);

        RemoteTestService proxiedService = (RemoteTestService) newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { RemoteTestService.class },
                handler);

        MicroserviceDescriptor descriptor = new MicroserviceDescriptor();
        descriptor.setName("testMS");
        descriptor.setHost("localhost");
        descriptor.setPort(9999);

        SharedServiceDescriptor sharedServiceDescriptor = new SharedServiceDescriptor();
        sharedServiceDescriptor.setServiceClassname(RemoteTestService.class.getName());
        sharedServiceDescriptor.setServiceName(RemoteTestService.class.getSimpleName());

        SharedServiceMethodDescriptor methodDescriptor = new SharedServiceMethodDescriptor();
        methodDescriptor.setName("getTestDomainObjectById");
        methodDescriptor.setReturnTypeClassName("java.util.Map");

        MethodArg arg = new MethodArg();
        arg.setName("id");
        arg.setType("java.lang.String");

        methodDescriptor.setArguments(asList(arg));

        Map<String, SharedServiceMethodDescriptor> methodDescriptors = new HashMap<>();
        methodDescriptors.put("getTestDomainObjectById", methodDescriptor);

        sharedServiceDescriptor.setMethods(methodDescriptors);

        descriptor.setServiceDescriptors(asList(sharedServiceDescriptor));

        when(mockedResolver.resolveService("testMS")).thenReturn(descriptor);
        when(mockedSocketRegistry.resolveSocket(descriptor)).thenReturn(mockedSocket);
        when(mockedSocket.getInputStream()).thenAnswer(invocation -> {
            byteArrayInputStream.reset();
            return byteArrayInputStream;
        });
        when(mockedSocket.getOutputStream()).thenReturn(mockedOutputStream);

        try {
            proxiedService.getTestDomainObjectById("123");
        } catch (CommunicationException e) {
            assertThat(e.getMessage(), containsString("invalid response"));
        }
    }
}
