package pl.insert.framework.transactional.entitymanager;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class EntityManagerHandler implements InvocationHandler {
    private final EntityManagerFactory entityManagerFactory;

    public EntityManagerHandler(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        EntityManager entityManager = TransactionSynchronizationManager.getEntityManager(entityManagerFactory);
        return method.invoke(entityManager, args);
    }
}
