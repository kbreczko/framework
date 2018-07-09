package pl.insert.framework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public class DynamicProxyInvocationHandler implements InvocationHandler {
    private Supplier<?> target;
    private AOPInterceptor interceptor;

    public DynamicProxyInvocationHandler(Supplier<?> target, AOPInterceptor interceptor) {
        this.target = target;
        this.interceptor = interceptor;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            interceptor.before(method, args);
            Object returnValue = method.invoke(target.get(), args);
            interceptor.after(method, args);
            return returnValue;
        } catch (Throwable t) {
            interceptor.afterThrowing(method, args, t);
            throw t;
        } finally {
            interceptor.afterFinally(method, args);
        }
    }
}
