package pl.insert.framework.transactional;

import pl.insert.framework.annotations.transactional.Transactional;
import pl.insert.framework.proxy.AOPInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TransactionalHandler implements InvocationHandler {
    private Object target;
    private AOPInterceptor interceptor;

    public TransactionalHandler(Object target, AOPInterceptor interceptor) {
        this.target = target;
        this.interceptor = interceptor;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isTransactional(method)) {
            try {
                interceptor.before(target, method, args);
                Object returnValue = method.invoke(target, args);
                interceptor.after(target, method, args);
                return returnValue;
            } catch (Throwable t) {
                interceptor.afterThrowing(target, method, args, t);
                throw t;
            } finally {
                interceptor.afterFinally(target, method, args);
            }
        }

        return method.invoke(target, args);
    }

    private boolean isTransactional(Method method) throws NoSuchMethodException {
        Class<?> clazz = target.getClass();
        return clazz.isAnnotationPresent(Transactional.class)
                || clazz.getMethod(method.getName(), method.getParameterTypes()).isAnnotationPresent(Transactional.class);
    }
}
