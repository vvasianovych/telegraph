package com.keebraa.telegraph;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.net.InetAddress.getLocalHost;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keebraa.telegraph.annotations.RemoteService;
import com.keebraa.telegraph.remote.ProxyMethodInvokationHandler;
import com.keebraa.telegraph.remote.RemoteServiceResolver;
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
    private static final String MULTICAST_ADDRESS_PARAM = "telegraph.communication.multicastAddress";
    private static final String MULTICAST_PORT_PARAM = "telegraph.communication.multicastPort";
    private static final String TELEGRAPH_HOST_PARAM = "telegraph.communication.host";
    private static final String TELEGRAPH_PORT_PARAM = "telegraph.communication.port";

    private static final String DEFAULT_MULTICAST_ADDRESS = "233.0.0.0";
    private static final Integer DEFAULT_TELEGRAPH_PORT = 9992;
    private static final Integer DEFAULT_MULTICAST_PORT = 9991;

    private static Logger log = LoggerFactory.getLogger("Telegraph");

    private BeanNameGenerator nameGenerator = new AnnotationBeanNameGenerator();

    private ObjectMapper objectMapper = new ObjectMapper();

    public void initialize(ConfigurableApplicationContext applicationContext) {

        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        String remoteServicePackage = environment.getProperty(REMOTE_SERVICE_PACKAGE_PARAM);

        ClassPathScanningCandidateComponentProvider interfaceProvider = createRemoteInterfaceComponentScanner();
        for (BeanDefinition beanDef : interfaceProvider.findCandidateComponents(remoteServicePackage)) {
            log.info("Found remote service: {}", beanDef.getBeanClassName());

            Class<?> remoteInterfaceClass = getRemoteServiceClass(beanDef.getBeanClassName());
            ProxyMethodInvokationHandler handler = new ProxyMethodInvokationHandler(remoteInterfaceClass, objectMapper, "", "", 1000);
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

        try {
            registerRequiredServices(applicationContext);
        } catch (UnknownHostException e) {
            log.error("Telegraph can't start. ", e);
            throw new RuntimeException(e);
        }
    }

    public void registerRequiredServices(ConfigurableApplicationContext applicationContext) throws UnknownHostException {
        SharedServicesRegistry registry = new SharedServicesRegistry();
        applicationContext.getBeanFactory().registerSingleton(registry.getClass().getName(), registry);
        log.info("Shared services registry is initialized.");

        String multicastAddress = applicationContext.getEnvironment().getProperty(MULTICAST_ADDRESS_PARAM, DEFAULT_MULTICAST_ADDRESS);
        Integer multicastPort = applicationContext.getEnvironment().getProperty(MULTICAST_PORT_PARAM, Integer.class, DEFAULT_MULTICAST_PORT);
        String localHost = applicationContext.getEnvironment().getProperty(TELEGRAPH_HOST_PARAM);
        Integer localPort = applicationContext.getEnvironment().getProperty(TELEGRAPH_PORT_PARAM, Integer.class, DEFAULT_TELEGRAPH_PORT);
        RemoteServiceResolver remoteServiceResolver = new RemoteServiceResolver(multicastAddress, multicastPort, localHost, localPort, objectMapper);
        applicationContext.getBeanFactory().registerSingleton(remoteServiceResolver.getClass().getName(), remoteServiceResolver);
        remoteServiceResolver.handleResponses();
        log.info("Remote Service Registry is initialized. Multicast address: {}, port: {}, Local address: {}, local port: {}", multicastAddress, multicastPort,
                localHost, localPort);

    }

    private Class<?> getRemoteServiceClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
