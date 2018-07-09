package pl.insert.framework.proxy;

import java.lang.reflect.Method;

public interface AOPInterceptor {
    void before(Object target, Method method, Object[] args) throws NoSuchMethodException;

    void after(Object target, Method method, Object[] args);

    void afterThrowing(Object target, Method method, Object[] args, Throwable throwable);

    void afterFinally(Object target, Method method, Object[] args);
}
