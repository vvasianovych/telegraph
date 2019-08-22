package com.keebraa.telegraph.remote.defaultmethods;

public class ToStringMethodHandler implements BasicMethodHandler {

    @Override
    public Object invoke(Class<?> interfaceClass, String name, String remoteHost, Object... args) {
        StringBuilder builder = new StringBuilder();
        builder.append("Remote Proxy ");
        builder.append("[interface: ");
        builder.append(interfaceClass.getCanonicalName());
        builder.append("; name: ");
        builder.append(name);
        builder.append("; remote host: ");
        builder.append(remoteHost);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String getMethodName() {
        return "toString";
    }
}
