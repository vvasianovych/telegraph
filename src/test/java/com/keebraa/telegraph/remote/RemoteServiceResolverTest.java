package com.keebraa.telegraph.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keebraa.telegraph.lib.MicroserviceDescriptor;

public class RemoteServiceResolverTest {

    @Test
    public void test_normalCase() throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();

        SuccessTestMulticastClient client = new SuccessTestMulticastClient("testMS", "localhost", 8877, "233.0.0.0", 9999, "localhost", 4444, mapper);
        client.run();

        RemoteServiceResolver resolver = new RemoteServiceResolver("233.0.0.0", 9999, "localhost", 4444, mapper);
        resolver.handleResponses();
        MicroserviceDescriptor resolvedDescriptor = resolver.resolveService("testMS");

        assertNotNull(resolvedDescriptor);
        assertEquals("localhost", resolvedDescriptor.getHost());
        assertEquals(8877, resolvedDescriptor.getPort());

        client.stop();
        resolver.stop();
    }

    @Test
    public void test_timedOutResponseNormalCase() throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();

        SuccessTestMulticastClient client = new SuccessTestMulticastClient("testMS", "localhost", 8877, "233.0.0.0", 9999, "localhost", 4444, mapper);
        client.setResponseTimeout(2000);
        client.run();
        
        RemoteServiceResolver resolver = new RemoteServiceResolver("233.0.0.0", 9999, "localhost", 4444, mapper);
        resolver.handleResponses();
        MicroserviceDescriptor resolvedDescriptor = resolver.resolveService("testMS");

        assertNotNull(resolvedDescriptor);
        assertEquals("localhost", resolvedDescriptor.getHost());
        assertEquals(8877, resolvedDescriptor.getPort());

        client.stop();
        resolver.stop();
    }
}
