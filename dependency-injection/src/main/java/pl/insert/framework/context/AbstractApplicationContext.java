package pl.insert.framework.context;

import pl.insert.framework.beans.BeanFactory;

public abstract class AbstractApplicationContext  implements ApplicationContext{
    @Override
    public void close() {
        getBeanFactory().destroyBeans();
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return getBeanFactory().getBean(requiredType);
    }

    protected abstract BeanFactory getBeanFactory();
}
