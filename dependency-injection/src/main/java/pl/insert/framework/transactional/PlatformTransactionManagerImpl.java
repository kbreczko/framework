package pl.insert.framework.transactional;

import pl.insert.framework.entitymanager.EntityManagerUnit;

import javax.persistence.EntityManager;

public class PlatformTransactionManagerImpl implements PlatformTransactionManager {
    private final EntityManagerUnit entityManagerUnit;

    public PlatformTransactionManagerImpl(EntityManagerUnit entityManagerUnit) {
        this.entityManagerUnit = entityManagerUnit;
    }

    @Override
    public EntityManager newTransaction() {
        return entityManagerUnit.createNewEntityManager();
    }

    @Override
    public void open(TransactionInfo transactionInfo) {
        EntityManager entityManager = transactionInfo.getEntityManager();
        if (!entityManager.getTransaction().isActive())
            entityManager.getTransaction().begin();
    }

    @Override
    public void rollBack(TransactionInfo transactionInfo) {
        transactionInfo.getEntityManager().getTransaction().setRollbackOnly();
    }

    @Override
    public void commit(TransactionInfo transactionInfo) {
        transactionInfo.getEntityManager().getTransaction().commit();
    }

    @Override
    public void closeIfStillOpen(TransactionInfo transactionInfo) {
        if (transactionInfo.isOpenTransaction())
            transactionInfo.getEntityManager().close();

    }
}
