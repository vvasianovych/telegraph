package com.keebraa.telegraph.remote.defaultmethods;

import com.keebraa.telegraph.annotations.RemoteService;

public class ToStringMethodHandler implements BasicMethodHandler {

    @Override
    public Object invoke(Class<?> interfaceClass, Object... args) {
        RemoteService remoteServiceAnnotation = interfaceClass.getAnnotation(RemoteService.class);
        StringBuilder builder = new StringBuilder();
        builder.append("Remote Proxy ");
        builder.append("[interface: ");
        builder.append(interfaceClass.getCanonicalName());
        if (remoteServiceAnnotation != null && remoteServiceAnnotation.serviceName() != null) {
            builder.append("; serviceName: ");
            builder.append(remoteServiceAnnotation.serviceName());
        }

        if (remoteServiceAnnotation != null && remoteServiceAnnotation.msName() != null) {
            builder.append("; msName: ");
            builder.append(remoteServiceAnnotation.msName());
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String getMethodName() {
        return "toString";
    }
}
