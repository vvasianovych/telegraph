package com.keebraa.telegraph.lib;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

/**
 * Microservice request structure
 * 
 * @author vvasianovych
 *
 */
public class MulticastMicroserviceDescriptorRequest {

    private String requestId;

    private String requesterHost;

    private int requesterPort;

    private List<String> microservices = new ArrayList<>();

    private long timestamp;

    
    public MulticastMicroserviceDescriptorRequest() {
    }

    public MulticastMicroserviceDescriptorRequest(String requestId, String requesterHost, int requesterPort, long timestamp, String... microservices) {
        this.requestId = requestId;
        this.requesterHost = requesterHost;
        this.requesterPort = requesterPort;
        this.microservices.addAll(asList(microservices));
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequesterHost() {
        return requesterHost;
    }

    public void setRequesterHost(String requesterHost) {
        this.requesterHost = requesterHost;
    }

    public int getRequesterPort() {
        return requesterPort;
    }

    public void setRequesterPort(int requesterPort) {
        this.requesterPort = requesterPort;
    }

    public List<String> getMicroservices() {
        return microservices;
    }

    public void setMicroservices(List<String> microservices) {
        this.microservices = microservices;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
