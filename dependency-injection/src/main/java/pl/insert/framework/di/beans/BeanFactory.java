package pl.insert.framework.di.beans;

import pl.insert.framework.di.beans.config.BeanPostProcessor;
import pl.insert.framework.di.support.BeanDefinition;

public interface BeanFactory {
    <T> T getBean(Class<T> requiredType);

    void registerBean(String beanName, BeanDefinition beanDefinition);

    void registerBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    void destroyBeans();
}
