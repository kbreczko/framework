package pl.insert.framework.di.beans;

import pl.insert.framework.transactional.exceptions.NoAnnotationException;

public interface BeanPostProcessor extends BeanFactoryAware{
    <T> T postProcess(T bean, String beanName) throws NoAnnotationException;
}
