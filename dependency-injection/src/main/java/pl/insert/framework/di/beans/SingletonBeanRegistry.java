package pl.insert.framework.di.beans;

public interface SingletonBeanRegistry {
    void registerSingleton(String beanName, Object singletonObject);

    <T> T getSingleton(String beanName);

    boolean containsSingleton(String beanName);

    void registerDisposableBean(DisposableBean disposableBean);

    void destroySingletons();
}
