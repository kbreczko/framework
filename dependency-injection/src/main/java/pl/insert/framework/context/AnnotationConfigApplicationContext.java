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
import pl.insert.framework.beans.BeanPostProcessor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;


public class AnnotationConfigApplicationContext extends AbstractApplicationContext implements ApplicationContext {
    private final static List<Class<? extends Annotation>> componentAnnotations = List.of(Service.class, Repository.class, Component.class);
    private final BeanFactory beanFactory = new BeanFactoryImpl();
    private final Object beanFactoryMonitor = new Object();

    public AnnotationConfigApplicationContext(Class<?> applicationConfiguration) {
        this();

        if (applicationConfiguration.isAnnotationPresent(ComponentScan.class))
            scanPackage(applicationConfiguration.getAnnotation(ComponentScan.class).value());

        registerBeansFromApplicationConfiguration(applicationConfiguration);
    }

    public AnnotationConfigApplicationContext() {
        loadBeanPostProcessor();
    }

    private void loadBeanPostProcessor(){
        ServiceLoader<BeanPostProcessor> beanPostProcessors = ServiceLoader.load(BeanPostProcessor.class);
        beanPostProcessors.forEach(beanPostProcessor -> beanPostProcessor.setBeanFactory(beanFactory));
        beanPostProcessors.forEach(beanFactory::registerBeanPostProcessor);
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
