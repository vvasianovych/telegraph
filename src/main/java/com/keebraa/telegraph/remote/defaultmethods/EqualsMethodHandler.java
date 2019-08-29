package com.keebraa.telegraph.remote.defaultmethods;

public class EqualsMethodHandler implements BasicMethodHandler {

    @Override
    public Object invoke(Class<?> interfaceClass, Object... args) {
        return false;
    }

    @Override
    public String getMethodName() {
        return "equals";
    }
}
