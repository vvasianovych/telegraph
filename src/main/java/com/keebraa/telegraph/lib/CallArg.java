package com.keebraa.telegraph.lib;

/**
 * Wraps argument information for remote method call.
 * 
 * @author vvasianovych
 *
 */
public class CallArg {

    private String type;

    private Object value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
