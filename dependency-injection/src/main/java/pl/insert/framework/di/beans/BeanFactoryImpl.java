package pl.insert.framework.di.beans;

import pl.insert.framework.common.util.ClassUtil;
import pl.insert.framework.di.annotations.Inject;
import pl.insert.framework.di.beans.config.BeanPostProcessor;
import pl.insert.framework.di.beans.config.DisposableBean;
import pl.insert.framework.di.exceptions.BeansException;
import pl.insert.framework.di.exceptions.NoSuchBeanDefinitionException;
import pl.insert.framework.di.exceptions.NoUniqueBeanDefinitionException;
import pl.insert.framework.di.support.BeanDefinition;

import javax.persistence.EntityManagerFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanFactoryImpl extends SingletonBeanRegistryImpl implements BeanFactory {
    private final Map<String, BeanDefinition> beanDefinitionMap;
    private final List<BeanPostProcessor> beanPostProcessors;
    private final List<Class<? extends Annotation>> dependencyInjectionAnnotations;

    public BeanFactoryImpl() {
        this.beanDefinitionMap = new HashMap<>();
        this.beanPostProcessors = new LinkedList<>();
        this.dependencyInjectionAnnotations = List.of(Inject.class);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        if (containsSingleton(requiredType.getName()))
            return getSingleton(requiredType.getName());

        T instance = createInstance(requiredType);
        registerSingleton(instance.getClass().getName(), instance);
        if (instance instanceof DisposableBean)
            registerDisposableBean((DisposableBean) instance);
        if (instance instanceof EntityManagerFactory)
            registerDisposableBean(((EntityManagerFactory) instance)::close);
        return instance;
    }

    @Override
    public void registerBean(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public void registerBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.add(beanPostProcessor);
    }

    @Override
    public void destroyBeans() {
        this.destroySingletons();
    }

    private <T> T createInstance(Class<T> requiredType) {
        BeanDefinition beanDefinition = getBeanDefinition(requiredType);
        T instance = (T) beanDefinition.createObject();
        resolveDependencies(instance);
        return createProxyFromBeanPostProcessors(instance, requiredType);
    }

    private <T> BeanDefinition getBeanDefinition(Class<T> requiredType) {
        String requiredTypeName = requiredType.getName();
        List<BeanDefinition> beanDefinitionsForRequiredType = beanDefinitionMap.entrySet().stream()
            .filter(entry -> isBeanDefinition(requiredTypeName, entry))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        if (beanDefinitionsForRequiredType.size() > 1)
            throw new NoUniqueBeanDefinitionException("No qualifying bean of type " + requiredTypeName + " is defined");

        return beanDefinitionsForRequiredType.stream().findFirst().orElseThrow(() -> new NoSuchBeanDefinitionException("No bean named '" + requiredTypeName + "' is defined"));
    }

    private boolean isBeanDefinition(String requiredTypeName, Map.Entry<String, BeanDefinition> entry) {
        Class<?> clazz = ClassUtil.forName(entry.getKey()).orElseThrow(() -> new BeansException("Class not found for:" + entry.getKey()));
        Class<?>[] interfaces = clazz.getInterfaces();
        Stream<Class<?>> interfacesStream = Arrays.stream(interfaces);
        return requiredTypeName.equals(entry.getKey()) || interfacesStream.anyMatch(iface -> iface.getName().equals(requiredTypeName));
    }

    private void resolveDependencies(Object instance) {
        Field[] declaredFields = instance.getClass().getDeclaredFields();
        Arrays.stream(declaredFields)
            .filter(field -> dependencyInjectionAnnotations.stream().anyMatch(field::isAnnotationPresent))
            .forEach(field -> ClassUtil.setField(field, instance, getBean(field.getType())));
    }

    private <T> T createProxyFromBeanPostProcessors(T instance, Class<T> requiredType){
        T proxyObject = instance;
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors)
            proxyObject = beanPostProcessor.postProcess(proxyObject, requiredType.getName());

        return proxyObject;
    }
}
