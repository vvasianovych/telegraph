package com.keebraa.telegraph.integration.application1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApplication1 implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting test app 1");
    }
}
