package com.keebraa.telegraph.remote.defaultmethods;

public class HashCodeMethodHandler implements BasicMethodHandler {

    @Override
    public Object invoke(Class<?> interfaceClass, Object... args) {
        return interfaceClass.hashCode();
    }

    @Override
    public String getMethodName() {
        return "hashCode";
    }
}
