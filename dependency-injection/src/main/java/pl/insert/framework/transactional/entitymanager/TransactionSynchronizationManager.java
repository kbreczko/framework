package pl.insert.framework.transactional.entitymanager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class TransactionSynchronizationManager {
    private static final ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<>();

    public static EntityManager getEntityManager(EntityManagerFactory emf) {
        EntityManager entityManager = entityManagerThreadLocal.get();
        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = emf.createEntityManager();
            entityManagerThreadLocal.set(entityManager);
        }
        return entityManager;
    }

    public static void setActualEntityManager(EntityManager entityManager) {
        entityManagerThreadLocal.set(entityManager);
    }

    public static EntityManager createNewEntityManager(EntityManagerFactory emf){
        EntityManager entityManager = emf.createEntityManager();
        setActualEntityManager(entityManager);
        return entityManager;
    }

}
