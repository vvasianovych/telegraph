package com.keebraa.telegraph.remote.defaultmethods;

public class EqualsMethodHandler implements BasicMethodHandler {

    @Override
    public Object invoke(Class<?> interfaceClass, String name, String remoteHost, Object... args) {
        return false;
    }

    @Override
    public String getMethodName() {
        return "equals";
    }
}
