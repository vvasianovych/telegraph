package com.keebraa.telegraph.lib;

import java.util.Map;

/**
 * Contains needed information about shared service.
 * 
 * @author vvasianovych
 *
 */
public class SharedServiceDescriptor {

    private String microserviceName;

    private String serviceClassname;

    private String serviceName;

    private Map<String, SharedServiceMethodDescriptor> methods;

    public String getMicroserviceName() {
        return microserviceName;
    }

    public void setMicroserviceName(String microserviceName) {
        this.microserviceName = microserviceName;
    }

    public String getServiceClassname() {
        return serviceClassname;
    }

    public void setServiceClassname(String serviceClassname) {
        this.serviceClassname = serviceClassname;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<String, SharedServiceMethodDescriptor> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, SharedServiceMethodDescriptor> methods) {
        this.methods = methods;
    }
}
