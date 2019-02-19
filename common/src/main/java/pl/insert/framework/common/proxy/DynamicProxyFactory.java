package pl.insert.framework.common.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class DynamicProxyFactory {

    public static <T> T createProxy(Class<T> iface, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{iface}, handler);
    }

    public static <T> T createProxy(Class<?>[] interfaces, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, handler);
    }
}
