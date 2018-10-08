package pl.insert.framework.proxy;


import pl.insert.framework.beans.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

public class DynamicProxyFactory {

    public static <T> T createProxy(Class<T> iface, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{iface}, handler);
    }

    public static <T> T createProxy(Class<?>[] interfaces, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, handler);
    }

    public static <T> T createProxy(T targetObject, Class<T> iface, List<BeanPostProcessor> beanPostProcessors) {
        T proxyObject = targetObject;
        if (beanPostProcessors.size() > 0)
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors)
                proxyObject = beanPostProcessor.postProcess(proxyObject, iface.getName());

        return proxyObject;
    }
}
