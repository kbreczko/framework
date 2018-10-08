package pl.insert.framework.beans;

import pl.insert.framework.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public final class BeanDefinitionImpl implements BeanDefinition {
    private Class<?> clazz;
    private Method method;
    private Class<?> resource;
    private Supplier<?> function;

    public BeanDefinitionImpl(Method method, Class<?> resource) {
        this.method = method;
        this.resource = resource;
    }

    public BeanDefinitionImpl(Class<?> clazz) {
        this.clazz = clazz;
    }

    public BeanDefinitionImpl(Supplier<?> function) {
        this.function = function;
    }

    @Override
    public Object createObject() {
        if(method != null && resource != null)
            return  ClassUtil.invokeMethod(ClassUtil.newInstance(resource).get(), method).get();

        if(clazz != null)
            return ClassUtil.newInstance(clazz).get();

        return function.get();
    }
}
