package com.keebraa.telegraph.integration.application2;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.keebraa.telegraph.annotations.SharedService;
import com.keebraa.telegraph.integration.domain.RemoteObject;

@Service
@SharedService(serviceName = "TestApplication2SharedService")
public class TestApplication2SharedService {

    public RemoteObject getObjectById(String id) {
        RemoteObject result = new RemoteObject();
        result.setId(id);
        result.setRandomValue("Random value");
        result.setTimestamp(Instant.now().toEpochMilli());
        return result;
    }
}
