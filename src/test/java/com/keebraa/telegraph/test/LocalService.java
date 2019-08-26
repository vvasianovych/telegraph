package com.keebraa.telegraph.test;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.customapp.services.UserRemoteService;
import com.keebraa.telegraph.annotations.SharedService;

@Service
@SharedService
public class LocalService {

    @Autowired
    private UserRemoteService userRemoteService;
    
    
    @PostConstruct
    public void init() {
        System.out.println(userRemoteService);
    }
}
