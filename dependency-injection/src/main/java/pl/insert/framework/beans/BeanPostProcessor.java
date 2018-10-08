package pl.insert.framework.beans;

public interface BeanPostProcessor {
    <T> T postProcess(T bean, String beanName);
}
