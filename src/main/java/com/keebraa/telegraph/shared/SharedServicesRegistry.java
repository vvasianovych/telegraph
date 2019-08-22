package com.keebraa.telegraph.shared;

import static java.net.InetAddress.getLocalHost;
import static org.springframework.util.StringUtils.hasText;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.keebraa.telegraph.annotations.SharedService;

/**
 * This class is registered as bean in context and handles all shared services.
 * 
 * @author vvasianovych
 *
 */
@Component
public class SharedServicesRegistry {

    private static Logger log = LoggerFactory.getLogger("Telegraph");

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${telegraph.microservice.name:}")
    private String microserviceName;

    private Map<String, Object> sharedServices = new HashMap<>();

    @PostConstruct
    public void init() {
        String hostName;

        try {
            hostName = getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostName = RandomStringUtils.randomAlphabetic(7);
            log.warn("Can't resolve hostname. Hostname will be auto-generated: {}", hostName);
        }

        microserviceName = hasText(microserviceName) ? microserviceName : hostName;
        Map<String, Object> sharedServices = applicationContext.getBeansWithAnnotation(SharedService.class);

        sharedServices.entrySet().forEach(entry -> {
            SharedService sharedServiceAnnotation = entry.getValue().getClass().getAnnotation(SharedService.class);
            String sharedServiceName = sharedServiceAnnotation.serviceName();
            sharedServiceName = hasText(sharedServiceName) ? sharedServiceName : entry.getKey();
            log.info("Register shared service: class {}, service name: {}", entry.getValue().getClass().getCanonicalName(), sharedServiceName);
            this.sharedServices.put(sharedServiceName, entry.getValue());
        });
    }
}
