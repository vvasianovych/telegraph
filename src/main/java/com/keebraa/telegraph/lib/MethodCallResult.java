package com.keebraa.telegraph.lib;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Contains low-level data about remote service call.
 * 
 * @author vvasianovych
 *
 */
@JsonInclude(NON_NULL)
public class MethodCallResult {
    
    private MethodCall call;

    private String returnType;

    private Object result;

    private CallStatus status;

    private Map<String, Object> exceptionDetails;

    public MethodCall getCall() {
        return call;
    }

    public void setCall(MethodCall call) {
        this.call = call;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public CallStatus getStatus() {
        return status;
    }

    public void setStatus(CallStatus status) {
        this.status = status;
    }

    public Map<String, Object> getExceptionDetails() {
        return exceptionDetails;
    }

    public void setExceptionDetails(Map<String, Object> exception) {
        this.exceptionDetails = exception;
    }
}
