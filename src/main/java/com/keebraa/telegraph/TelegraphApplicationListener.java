package com.keebraa.telegraph;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import com.keebraa.telegraph.annotations.SharedService;
import com.keebraa.telegraph.shared.SharedServicesRegistry;

public class TelegraphApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final String MICROSERVICE_NAME = "telegraph.microservice.name";

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, Object> beans = context.getBeansWithAnnotation(SharedService.class);
        if (beans != null && !beans.isEmpty()) {
            String microserviceName = event.getApplicationContext().getEnvironment().getProperty(MICROSERVICE_NAME);
            SharedServicesRegistry registry = new SharedServicesRegistry(microserviceName, beans);
            if (context instanceof ConfigurableApplicationContext) {
                ((ConfigurableApplicationContext) context).getBeanFactory().registerSingleton(SharedServicesRegistry.class.getName(), registry);
            }
        }
    }
}
