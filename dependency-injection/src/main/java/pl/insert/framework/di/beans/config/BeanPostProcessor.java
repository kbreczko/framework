package pl.insert.framework.di.beans.config;

public interface BeanPostProcessor extends BeanFactoryAware {
    <T> T postProcess(T bean, String beanName);
}
