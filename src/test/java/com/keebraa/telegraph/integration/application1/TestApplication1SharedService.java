package com.keebraa.telegraph.integration.application1;

import com.keebraa.telegraph.annotations.RemoteService;
import com.keebraa.telegraph.integration.domain.RemoteObject;

@RemoteService(serviceName = "TestApplication2SharedService", msName = "test-application-2")
public interface TestApplication1SharedService {

    RemoteObject getObjectById(String id);
}
