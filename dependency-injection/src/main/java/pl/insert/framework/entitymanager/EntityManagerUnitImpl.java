package pl.insert.framework.entitymanager;

import pl.insert.framework.adnotations.components.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.function.Supplier;

@Service
public class EntityManagerUnitImpl implements Supplier<EntityManager>, EntityManagerUnit {

    private final ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();
    private EntityManagerFactory entityManagerFactory;

    public EntityManagerUnitImpl(EntityManagerFactory entityManagerFactory) {
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

    @Override
    public void set(EntityManager entityManager) {
        entityManagerThreadLocal.set(entityManager);
    }

    @Override
    public EntityManager createNewEntityManager() {
        EntityManager entityManager = getEntityManagerFactory().createEntityManager();
        entityManagerThreadLocal.set(entityManager);
        return entityManager;
    }

    private EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null)
            throw new NullPointerException("No bean named 'EntityManagerFactory' is defined");

        return entityManagerFactory;
    }
}