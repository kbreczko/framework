package pl.insert.entitymanager;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public class EntityManagerHandler implements InvocationHandler {

    private Supplier<EntityManager> entityManagerSupplier;

    public EntityManagerHandler(Supplier<EntityManager> entityManagerSupplier) {
        this.entityManagerSupplier = entityManagerSupplier;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(entityManagerSupplier.get(), args);
    }
}
