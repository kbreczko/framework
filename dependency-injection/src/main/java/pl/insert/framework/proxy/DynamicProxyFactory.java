package pl.insert.framework.proxy;

import java.lang.reflect.InvocationHandler;

public interface DynamicProxyFactory {
    <T> T createProxy(Class<T> clazz, InvocationHandler handler);
}
