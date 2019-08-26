package com.keebraa.telegraph.lib;

import java.util.Map;

/**
 * Contains needed information about shared service.
 * 
 * @author vvasianovych
 *
 */
public class SharedServiceDescriptor {

    private String microserviceName;
    
    private String serviceClassname;

    private String serviceName;

    private Map<String, SharedServiceMethodDescriptor> methods;
}
