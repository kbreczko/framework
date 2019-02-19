package pl.insert.framework.di.beans;

import pl.insert.framework.di.beans.config.DisposableBean;

public interface SingletonBeanRegistry {
    void registerSingleton(String beanName, Object singletonObject);

    <T> T getSingleton(String beanName);

    boolean containsSingleton(String beanName);

    void registerDisposableBean(DisposableBean disposableBean);

    void destroySingletons();
}
