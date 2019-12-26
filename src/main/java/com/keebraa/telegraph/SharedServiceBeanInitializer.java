package com.keebraa.telegraph;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SharedServiceBeanInitializer implements ApplicationContextAware {

    @PostConstruct
    public void init() {
        System.out.println(123);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println(123);
    }
}
