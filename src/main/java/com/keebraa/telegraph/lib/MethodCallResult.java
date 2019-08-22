package com.keebraa.telegraph.lib;

/**
 * Contains low-level data about remote service call.
 * 
 * @author vvasianovych
 *
 */
public class MethodCallResult {
    
    private MethodCall call;

    private String returnType;

    private Object result;

    private CallStatus status;

    private Throwable exception;

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

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
