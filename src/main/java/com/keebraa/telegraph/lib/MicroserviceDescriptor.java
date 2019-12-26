package com.keebraa.telegraph.lib;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains needed data about microservice
 * 
 * @author vvasianovych
 *
 */
public class MicroserviceDescriptor {

    private String name;

    private String host;

    private int port;

    private String pubKey;

    private long ttl;

    private long lastSynced;

    private List<SharedServiceDescriptor> serviceDescriptors = new ArrayList<>();

    /**
     * Represents id of multicast request, that this descriptor will be sent for.
     */
    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<SharedServiceDescriptor> getServiceDescriptors() {
        return serviceDescriptors;
    }

    public void setServiceDescriptors(List<SharedServiceDescriptor> serviceDescriptors) {
        this.serviceDescriptors = serviceDescriptors;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long getLastSynced() {
        return lastSynced;
    }

    public void setLastSynced(long lastSynced) {
        this.lastSynced = lastSynced;
    }
}
