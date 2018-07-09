package pl.insert.framework.entitymanager;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public class EntityManagerHandler implements InvocationHandler {
    private Supplier<?> target;

    public EntityManagerHandler(Supplier<?> target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(target.get(), args);
    }
}
