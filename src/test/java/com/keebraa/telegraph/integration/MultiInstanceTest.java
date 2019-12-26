package com.keebraa.telegraph.integration;

import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.keebraa.telegraph.integration.application1.TestApplication1;
import com.keebraa.telegraph.integration.application2.TestApplication2;

public class MultiInstanceTest {

    @Test
    public void test() throws InterruptedException {
        System.setProperty("spring.main.web-application-type", "none");
        System.setProperty("telegraph.communication.host", "localhost");
        System.setProperty("telegraph.communication.port", "8888");
        System.setProperty("telegraph.services.packages", "com.keebraa.telegraph.integration.application1");
        System.setProperty("telegraph.microservice.name", "test-application-1");
        
        ConfigurableApplicationContext context1 = SpringApplication.run(TestApplication1.class);

        Thread.sleep(2000);
        
        System.setProperty("spring.main.web-application-type", "none");
        System.setProperty("telegraph.communication.host", "localhost");
        System.setProperty("telegraph.communication.port", "9999");
        System.setProperty("telegraph.services.packages", "com.keebraa.telegraph.integration.application2");
        System.setProperty("telegraph.microservice.name", "test-application-2");
        
        ConfigurableApplicationContext context2 = SpringApplication.run(TestApplication2.class);
        context2.start();
    }
}
