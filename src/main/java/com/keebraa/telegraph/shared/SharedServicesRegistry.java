package com.keebraa.telegraph.shared;

import static java.net.InetAddress.getLocalHost;
import static org.springframework.util.StringUtils.hasText;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.keebraa.telegraph.annotations.SharedService;

/**
 * This class is registered as bean in context and handles all shared services.
 * 
 * @author vvasianovych
 *
 */
public class SharedServicesRegistry {

    private static Logger log = LoggerFactory.getLogger("Telegraph");

    private Map<String, Object> sharedServices = new HashMap<>();

    public SharedServicesRegistry(String microserviceName, Map<String, Object> sharedServices) {
        init(microserviceName, sharedServices);
    }

    public void init(String microserviceName, Map<String, Object> sharedServices) {
        String hostName;

        try {
            hostName = getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostName = RandomStringUtils.randomAlphabetic(7);
            log.warn("Can't resolve hostname. Hostname will be auto-generated: {}", hostName);
        }

        microserviceName = hasText(microserviceName) ? microserviceName : hostName;

        log.info("Starting Telegraph instance. Application MS name is: '{}'", microserviceName);

        sharedServices.entrySet().forEach(entry -> {
            SharedService sharedServiceAnnotation = entry.getValue().getClass().getAnnotation(SharedService.class);
            String sharedServiceName = sharedServiceAnnotation.serviceName();
            sharedServiceName = hasText(sharedServiceName) ? sharedServiceName : entry.getKey();
            log.info("Register shared service: class {}, service name: {}", entry.getValue().getClass().getCanonicalName(), sharedServiceName);
            this.sharedServices.put(sharedServiceName, entry.getValue());
        });
    }
}
