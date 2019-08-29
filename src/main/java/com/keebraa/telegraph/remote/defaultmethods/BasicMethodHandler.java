package com.keebraa.telegraph.remote.defaultmethods;

public interface BasicMethodHandler {
    String getMethodName();
    Object invoke(Class<?> remoteInterfaceClass, Object...args);
}
