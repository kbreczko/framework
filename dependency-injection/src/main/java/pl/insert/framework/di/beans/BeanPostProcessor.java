package pl.insert.framework.di.beans;

public interface BeanPostProcessor extends BeanFactoryAware{
    <T> T postProcess(T bean, String beanName);
}
