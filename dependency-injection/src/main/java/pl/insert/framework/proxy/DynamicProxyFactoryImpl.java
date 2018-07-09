package pl.insert.framework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

public class DynamicProxyFactoryImpl implements DynamicProxyFactory {
    @Override
    public <T> T createProxy(Class<T> clazz, Supplier<T> target, AOPInterceptor interceptor) {
        InvocationHandler handler = new DynamicProxyInvocationHandler(target, interceptor);
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{clazz}, handler);
    }
}
