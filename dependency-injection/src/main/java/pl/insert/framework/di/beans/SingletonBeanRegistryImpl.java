package pl.insert.framework.di.beans;

import pl.insert.framework.common.util.ClassUtil;
import pl.insert.framework.di.beans.config.DisposableBean;
import pl.insert.framework.di.exceptions.BeansException;
import pl.insert.framework.di.exceptions.NoUniqueBeanDefinitionException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SingletonBeanRegistryImpl implements SingletonBeanRegistry {
    private List<DisposableBean> disposableBeans = new LinkedList<>();
    private final Map<String, Object> singletonObjects = new HashMap<>();

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }

    @Override
    public <T> T getSingleton(String beanName) {
        List<Object> candidates = singletonObjects.entrySet().stream()
            .filter(entry -> isImplementation(beanName, entry))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        if (candidates.size() > 1)
            throw new NoUniqueBeanDefinitionException("No qualifying bean of type " + beanName + " is defined");

        return (T) candidates.stream().findFirst().get();
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return singletonObjects.entrySet().stream().anyMatch(entry -> isImplementation(beanName, entry));
    }

    private boolean isImplementation(String beanName, Map.Entry<String, Object> entry) {
        Class<?> clazz = ClassUtil.forName(entry.getKey()).orElseThrow(() -> new BeansException("Class not found for:" + entry.getKey()));
        Class<?>[] interfaces = clazz.getInterfaces();
        Stream<String> interfacesStream = Arrays.stream(interfaces).map(Class::getName);
        return beanName.equals(entry.getKey()) || interfacesStream.anyMatch(beanName::equals);
    }

    @Override
    public void registerDisposableBean(DisposableBean disposableBean) {
        disposableBeans.add(disposableBean);
    }

    @Override
    public void destroySingletons() {
        disposableBeans.forEach(DisposableBean::destroy);
    }
}
