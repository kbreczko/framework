package pl.insert.framework.di.support;

import pl.insert.framework.common.util.ClassUtil;
import pl.insert.framework.di.exceptions.BeansException;

import java.lang.reflect.Method;
import java.util.Optional;

public final class BeanDefinitionImpl implements BeanDefinition {
    private Class<?> clazz;
    private Method method;
    private Class<?> resource;

    public BeanDefinitionImpl(Method method, Class<?> resource) {
        this.method = method;
        this.resource = resource;
    }

    public BeanDefinitionImpl(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object createObject() {
        if (method != null && resource != null)
            return createUsingMethod();

        return createUsingClass();
    }

    private Object createUsingMethod() {
        Optional resourceInstanceOptional = ClassUtil.newInstance(resource);

        if (resourceInstanceOptional.isEmpty())
            throw new BeansException("Object named " + resource.getName() + " can not be created");

        Optional instanceOptional = ClassUtil.invokeMethod(resourceInstanceOptional.get(), method);

        if (instanceOptional.isEmpty())
            throw new BeansException("Can not execute the method named: " + method.getName() + " for the object: " + resource.getName());

        return instanceOptional.get();
    }

    private Object createUsingClass() {
        Optional instanceOptional = ClassUtil.newInstance(clazz);

        if (instanceOptional.isEmpty())
            throw new BeansException("Object named " + clazz.getName() + " can not be created");

        return instanceOptional.get();
    }
}
