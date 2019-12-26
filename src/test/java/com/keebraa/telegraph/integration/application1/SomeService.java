package com.keebraa.telegraph.integration.application1;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keebraa.telegraph.shared.SharedServicesRegistry;

//@Service
public class SomeService {

    @Autowired
    private SharedServicesRegistry registry;
    
    @PostConstruct
    public void init() {
        System.out.println(123);
    }
}
