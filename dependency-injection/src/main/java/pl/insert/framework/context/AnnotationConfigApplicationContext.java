package pl.insert.framework.context;

import org.reflections.Reflections;
import pl.insert.framework.annotations.Bean;
import pl.insert.framework.annotations.ComponentScan;
import pl.insert.framework.annotations.components.Component;
import pl.insert.framework.annotations.components.Repository;
import pl.insert.framework.annotations.components.Service;
import pl.insert.framework.beans.BeanDefinitionImpl;
import pl.insert.framework.beans.BeanFactory;
import pl.insert.framework.beans.BeanFactoryImpl;
import pl.insert.framework.entitymanager.EntityManagerHandler;
import pl.insert.framework.entitymanager.EntityManagerPostProcessor;
import pl.insert.framework.entitymanager.EntityManagerUnit;
import pl.insert.framework.entitymanager.EntityManagerUnitImpl;
import pl.insert.framework.proxy.DynamicProxyFactory;
import pl.insert.framework.transactional.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;


public class AnnotationConfigApplicationContext extends AbstractApplicationContext implements ApplicationContext {
    private List<Class<? extends Annotation>> componentAnnotations = List.of(Service.class, Repository.class, Component.class);
    private BeanFactory beanFactory = new BeanFactoryImpl();
    private final Object beanFactoryMonitor = new Object();

    public AnnotationConfigApplicationContext(Class<?> applicationConfiguration) {
        this();

        if (applicationConfiguration.isAnnotationPresent(ComponentScan.class))
            scanPackage(applicationConfiguration.getAnnotation(ComponentScan.class).value());

        registerBeansFromApplicationConfiguration(applicationConfiguration);
    }

    public AnnotationConfigApplicationContext() {
        BeanDefinitionImpl beanDefinition = new BeanDefinitionImpl(() -> new EntityManagerUnitImpl(getBean(EntityManagerFactory.class)));
        getBeanFactory().registerBean(EntityManagerUnit.class.getName(), beanDefinition);

        beanDefinition = new BeanDefinitionImpl(() -> DynamicProxyFactory.createProxy(EntityManager.class, new EntityManagerHandler(getBean(EntityManagerUnit.class))));
        getBeanFactory().registerBean(EntityManager.class.getName(), beanDefinition);

        beanDefinition = new BeanDefinitionImpl(() -> new PlatformTransactionManagerImpl(getBean(EntityManagerUnit.class)));
        getBeanFactory().registerBean(PlatformTransactionManager.class.getName(), beanDefinition);

        beanDefinition = new BeanDefinitionImpl(() -> new TransactionalInterceptorImpl(getBean(PlatformTransactionManager.class)));
        getBeanFactory().registerBean(TransactionalInterceptor.class.getName(), beanDefinition);

        getBeanFactory().registerBeanPostProcessor(new EntityManagerPostProcessor());
        getBeanFactory().registerBeanPostProcessor(new TransactionalPostProcessor(() -> getBean(TransactionalInterceptor.class)));
    }

    private void scanPackage(String packageName) {
        Reflections reflections = new Reflections(packageName);

        componentAnnotations.stream()
            .flatMap(component -> reflections.getTypesAnnotatedWith(component).stream())
            .forEach(type -> getBeanFactory().registerBean(type.getName(), new BeanDefinitionImpl(type)));
    }

    private void registerBeansFromApplicationConfiguration(Class<?> applicationConfiguration) {
        Arrays.stream(applicationConfiguration.getMethods())
            .filter(method -> method.isAnnotationPresent(Bean.class))
            .forEach(method -> getBeanFactory().registerBean(method.getReturnType().getName(), new BeanDefinitionImpl(method, applicationConfiguration)));
    }

    protected BeanFactory getBeanFactory() {
        synchronized (beanFactoryMonitor) {
            return beanFactory;
        }
    }
}
