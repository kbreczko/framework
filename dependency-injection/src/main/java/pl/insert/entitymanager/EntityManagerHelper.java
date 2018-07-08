package pl.insert.entitymanager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.function.Supplier;

public class EntityManagerHelper implements Supplier<EntityManager> {

    private final ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();
    private EntityManagerFactory entityManagerFactory;

    public EntityManagerHelper(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public EntityManager get() {
        EntityManager entityManager = entityManagerThreadLocal.get();
        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = getEntityManagerFactory().createEntityManager();
            entityManagerThreadLocal.set(entityManager);
        }
        return entityManager;
    }

    private EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null)
            throw new NullPointerException("No bean named 'EntityManagerFactory' is defined");

        return entityManagerFactory;
    }
}