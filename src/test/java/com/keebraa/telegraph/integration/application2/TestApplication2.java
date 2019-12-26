package com.keebraa.telegraph.integration.application2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApplication2 implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting test app 2");
    }
}