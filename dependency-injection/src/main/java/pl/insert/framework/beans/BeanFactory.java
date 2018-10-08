package pl.insert.framework.beans;

public interface BeanFactory {
    <T> T getBean(Class<T> requiredType);

    void registerBean(String beanName, BeanDefinition beanDefinition);

    void registerBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    void destroyBeans();
}
