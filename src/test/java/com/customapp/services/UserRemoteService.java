package com.customapp.services;

import com.keebraa.telegraph.annotations.RemoteService;

@RemoteService(msName = "user-ms")
public interface UserRemoteService {
    String getUserId();
}