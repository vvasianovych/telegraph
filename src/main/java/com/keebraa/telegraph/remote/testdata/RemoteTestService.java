package com.keebraa.telegraph.remote.testdata;

import com.keebraa.telegraph.annotations.RemoteService;

/**
 * Represents just the test remote service that fetches some test domain object
 * by its id.
 * 
 * @author vvasianovych
 *
 */
@RemoteService(msName = "testMS")
public interface RemoteTestService {

    TestDomainObject getTestDomainObjectById(String id);
}
