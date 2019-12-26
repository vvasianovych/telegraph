package com.keebraa.telegraph.lib;

import java.util.List;

/**
 * Contains shared service's method data
 * 
 * @author vvasianovych
 *
 */
public class SharedServiceMethodDescriptor {

    private String name;

    private String returnTypeClassName;

    private List<MethodArg> arguments;

    public SharedServiceMethodDescriptor() {

    }

    public SharedServiceMethodDescriptor(String name, String returnTypeClassName, List<MethodArg> arguments) {
        this.name = name;
        this.returnTypeClassName = returnTypeClassName;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnTypeClassName() {
        return returnTypeClassName;
    }

    public void setReturnTypeClassName(String returnTypeClassName) {
        this.returnTypeClassName = returnTypeClassName;
    }

    public List<MethodArg> getArguments() {
        return arguments;
    }

    public void setArguments(List<MethodArg> arguments) {
        this.arguments = arguments;
    }
}
