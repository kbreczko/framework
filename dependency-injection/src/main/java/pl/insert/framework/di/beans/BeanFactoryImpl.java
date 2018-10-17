package pl.insert.framework.di.beans;

import pl.insert.framework.di.annotations.Inject;
import pl.insert.framework.di.exceptions.NoSuchBeanDefinitionException;
import pl.insert.framework.di.exceptions.NoUniqueBeanDefinitionException;
import pl.insert.framework.proxy.DynamicProxyFactory;
import pl.insert.framework.util.ClassUtil;

import javax.persistence.EntityManagerFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanFactoryImpl extends SingletonBeanRegistryImpl implements BeanFactory {
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final List<BeanPostProcessor> beanPostProcessors = new LinkedList<>();
    private final List<Class<? extends Annotation>> dependencyInjectionAnnotations = List.of(Inject.class);

    @Override
    public <T> T getBean(Class<T> requiredType) {
        if (containsSingleton(requiredType.getName()))
            return getSingleton(requiredType.getName());

        T instance = createInstance(requiredType);
        registerSingleton(instance.getClass().getName(), instance);
        if (instance instanceof DisposableBean )
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
        return DynamicProxyFactory.createProxy(instance, requiredType, beanPostProcessors);
    }

    private <T> BeanDefinition getBeanDefinition(Class<T> requiredType) {
        String requiredTypeName = requiredType.getName();
        List<BeanDefinition> beanDefinitionsForRequiredType = beanDefinitionMap.entrySet().stream()
            .filter(entry -> {
                Stream<Class<?>> interfacesStream = Arrays.stream(ClassUtil.forName(entry.getKey()).get().getInterfaces());
                return requiredTypeName.equals(entry.getKey()) || interfacesStream.anyMatch(iface -> iface.getName().equals(requiredTypeName));
            })
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        if (beanDefinitionsForRequiredType.size() > 1)
            throw new NoUniqueBeanDefinitionException("No qualifying bean of type " + requiredTypeName +" is defined");

        return beanDefinitionsForRequiredType.stream().findFirst().orElseThrow(() -> new NoSuchBeanDefinitionException("No bean named '" + requiredTypeName + "' is defined"));
    }

    private void resolveDependencies(Object instance) {
        Field[] declaredFields = instance.getClass().getDeclaredFields();
        Arrays.stream(declaredFields)
            .filter(field -> dependencyInjectionAnnotations.stream().anyMatch(field::isAnnotationPresent))
            .forEach(field -> ClassUtil.setField(field, instance, getBean(field.getType())));
    }
}
