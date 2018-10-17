package pl.insert.framework.transactional;

import pl.insert.framework.transactional.annotations.Transactional;
import pl.insert.framework.transactional.utils.AnnotationUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TransactionalHandler implements InvocationHandler {
    private Object target;
    private TransactionalInterceptor interceptor;

    public TransactionalHandler(Object target, TransactionalInterceptor interceptor) {
        this.target = target;
        this.interceptor = interceptor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (AnnotationUtils.hasMethodWithAnnotation(target.getClass(), method, Transactional.class)) {
            try {
                interceptor.before(target, method, args);
                Object returnValue = method.invoke(target, args);
                interceptor.after(target, method, args);
                return returnValue;
            } catch (Throwable t) {
                interceptor.afterThrowing(target, method, args, t);
                throw t.getCause();
            } finally {
                interceptor.afterFinally(target, method, args);
            }
        }

        return method.invoke(target, args);
    }
}
