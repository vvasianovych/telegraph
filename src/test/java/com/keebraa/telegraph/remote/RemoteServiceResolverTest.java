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

        SuccessTestMulticastClient client = new SuccessTestMulticastClient("testMS", "localhost", 8877, "233.0.0.0", 9999, "localhost", 4444, 10000, mapper);
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

        SuccessTestMulticastClient client = new SuccessTestMulticastClient("testMS", "localhost", 8877, "233.0.0.0", 9999, "localhost", 4444, 10000, mapper);
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
    
    @Test
    public void test_oldCacheRenewCase() throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();

        SuccessTestMulticastClient client1 = new SuccessTestMulticastClient("testMS", "localhost", 8877, "233.0.0.0", 9999, "localhost", 4444, 5000, mapper);
        client1.setResponseTimeout(2000);
        client1.run();
        
        RemoteServiceResolver resolver = new RemoteServiceResolver("233.0.0.0", 9999, "localhost", 4444, mapper);
        resolver.handleResponses();
        MicroserviceDescriptor resolvedDescriptor = resolver.resolveService("testMS");

        assertNotNull(resolvedDescriptor);
        assertEquals("localhost", resolvedDescriptor.getHost());
        assertEquals(8877, resolvedDescriptor.getPort());

        //stop previous client. Run another one with another data
        client1.stop();
        SuccessTestMulticastClient client2 = new SuccessTestMulticastClient("testMS", "192.168.99.99", 8877, "233.0.0.0", 9999, "localhost", 4444, 1000, mapper);
        client2.setResponseTimeout(2000);
        client2.run();
        
        //Now the response will be cached one
        resolvedDescriptor = resolver.resolveService("testMS");
        assertNotNull(resolvedDescriptor);
        assertEquals("localhost", resolvedDescriptor.getHost());
        assertEquals(8877, resolvedDescriptor.getPort());

        //Now let's wait another 5 seconds to get the cached value became old - then we have to get new values from client2.
        Thread.sleep(5000);
        
        resolvedDescriptor = resolver.resolveService("testMS");
        
        assertNotNull(resolvedDescriptor);
        assertEquals("192.168.99.99", resolvedDescriptor.getHost());
        assertEquals(8877, resolvedDescriptor.getPort());
        
        resolver.stop();
        client2.stop();
    }
}
