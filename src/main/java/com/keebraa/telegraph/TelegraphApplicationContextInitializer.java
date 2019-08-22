package com.keebraa.telegraph;

import static java.lang.reflect.Proxy.newProxyInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.keebraa.telegraph.annotations.RemoteService;
import com.keebraa.telegraph.remote.ProxyMethodInvokationHandler;
import com.keebraa.telegraph.shared.SharedServicesRegistry;

/**
 * Listener for application context, that adds all remote interfaces found in
 * application with proxies to application context.a
 * 
 * @author vvasianovych
 *
 */
public class TelegraphApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String REMOTE_SERVICE_PACKAGE_PARAM = "telegraph.services.packages";
    
    private static Logger log = LoggerFactory.getLogger("Telegraph");

    private BeanNameGenerator nameGenerator = new AnnotationBeanNameGenerator();

    public void initialize(ConfigurableApplicationContext applicationContext) {

        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String remoteServicePackage = environment.getProperty(REMOTE_SERVICE_PACKAGE_PARAM);

        ClassPathScanningCandidateComponentProvider interfaceProvider = createRemoteInterfaceComponentScanner();
        for (BeanDefinition beanDef : interfaceProvider.findCandidateComponents(remoteServicePackage)) {
            log.info("Found remote service: {}", beanDef.getBeanClassName());

            Class<?> remoteInterfaceClass = getRemoteServiceClass(beanDef.getBeanClassName());
            ProxyMethodInvokationHandler handler = new ProxyMethodInvokationHandler(remoteInterfaceClass, "", "");
            Object remoteService = newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { remoteInterfaceClass }, handler);

            RemoteService remoteServiceAnnotation = remoteInterfaceClass.getAnnotation(RemoteService.class);

            String serviceName = remoteInterfaceClass.getTypeName();

            if (applicationContext instanceof BeanDefinitionRegistry) {
                serviceName = nameGenerator.generateBeanName(beanDef, (BeanDefinitionRegistry) applicationContext);
            }
            if (!remoteServiceAnnotation.serviceName().trim().equals("")) {
                serviceName = remoteServiceAnnotation.serviceName();
            }

            String msName = remoteServiceAnnotation.msName();

            applicationContext.getBeanFactory().registerSingleton(remoteInterfaceClass.getName(), remoteService);
            log.debug("remote service was added to Context. interface class: {}, service name: {}, microservice name: {}",
                    remoteInterfaceClass.getCanonicalName(), serviceName, msName);
        }

        SharedServicesRegistry registry = new SharedServicesRegistry();
        applicationContext.getBeanFactory().registerSingleton(registry.getClass().getName(), registry);
        log.info("Shared services registry is initialized.");
    }

    private Class<?> getRemoteServiceClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * This provider searches only for interfaces, which means that it will be
     * remote interfaces. So, it searches for interfaces marked as @RemoteService.
     * 
     * @return provider that used to find RemoteService interfaces
     */
    private ClassPathScanningCandidateComponentProvider createRemoteInterfaceComponentScanner() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                AnnotationMetadata metadata = beanDefinition.getMetadata();
                return metadata.isIndependent() && metadata.isInterface();
            }
        };
        provider.addIncludeFilter(new AnnotationTypeFilter(RemoteService.class));
        return provider;
    }
}
