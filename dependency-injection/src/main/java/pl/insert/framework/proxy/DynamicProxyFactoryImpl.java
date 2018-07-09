package pl.insert.framework.proxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class DynamicProxyFactoryImpl implements DynamicProxyFactory {
    @Override
    public <T> T createProxy(Class<T> clazz, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{clazz}, handler);
    }
}
