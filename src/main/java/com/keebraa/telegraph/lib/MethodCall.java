package com.keebraa.telegraph.lib;

import java.util.List;

/**
 * Contains all needed details to call remote service
 * 
 * @author vvasianovych
 *
 */
public class MethodCall {

    private String hostName;

    private String serviceName;

    private String methodName;

    private List<MethodArg> args;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<MethodArg> getArgs() {
        return args;
    }

    public void setArgs(List<MethodArg> args) {
        this.args = args;
    }
}
