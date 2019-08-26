package com.keebraa.telegraph.lib;

/**
 * Wraps argument information for remote method call.
 * 
 * @author vvasianovych
 *
 */
public class MethodArg {

    private String type;

    private String name;

    private Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
