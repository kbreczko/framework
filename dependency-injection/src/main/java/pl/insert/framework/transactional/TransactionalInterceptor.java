package pl.insert.framework.transactional;

import java.lang.reflect.Method;

public interface TransactionalInterceptor {
    void before(Object target, Method method, Object[] args) throws NoSuchMethodException;

    void after(Object target, Method method, Object[] args);

    void afterThrowing(Object target, Method method, Object[] args, Throwable throwable);

    void afterFinally(Object target, Method method, Object[] args);
}
