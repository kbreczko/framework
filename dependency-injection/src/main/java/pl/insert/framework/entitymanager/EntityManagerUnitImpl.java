package pl.insert.framework.entitymanager;

import pl.insert.framework.exceptions.NoSuchBeanDefinitionException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EntityManagerUnitImpl implements EntityManagerUnit {
    private final ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();
    private final EntityManagerFactory entityManagerFactory;

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

    @Override
    public void destroy() {
        getEntityManagerFactory().close();
    }

    private EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null)
            throw new NoSuchBeanDefinitionException("No bean named 'EntityManagerFactory' is defined");

        return entityManagerFactory;
    }
}