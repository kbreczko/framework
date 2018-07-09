package pl.insert.proxy;

import java.util.function.Supplier;

public interface DynamicProxyFactory {
    <T> T createProxy(Class<T> clazz, Supplier<T> target, AOPInterceptor interceptor);
}
