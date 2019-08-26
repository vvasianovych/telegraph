package com.keebraa.telegraph.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.customapp.services.UserRemoteService;
import com.keebraa.telegraph.annotations.SharedService;

@Service
@SharedService
public class LocalService {

    @Autowired
    private UserRemoteService userRemoteService;
}
