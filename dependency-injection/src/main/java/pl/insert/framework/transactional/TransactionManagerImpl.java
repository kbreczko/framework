package pl.insert.framework.transactional;

import pl.insert.framework.transactional.entitymanager.TransactionSynchronizationManager;
import pl.insert.framework.transactional.enums.Propagation;
import pl.insert.framework.transactional.models.TransactionInfo;
import pl.insert.framework.transactional.models.TransactionalAttribute;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class TransactionManagerImpl implements TransactionManager {
    private final EntityManagerFactory entityManagerFactory;

    public TransactionManagerImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void restoreTransaction(TransactionInfo transactionInfo) {
        TransactionSynchronizationManager.setActualEntityManager(transactionInfo.getEntityManager());
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
        if (transactionInfo.isOpenTransaction() && transactionInfo.isNewTransaction() || transactionInfo.getOldTransactionInfo() != null)
            transactionInfo.getEntityManager().close();
    }

    @Override
    public TransactionInfo createTransactionInfo(TransactionalAttribute transactionalAttribute, TransactionInfo transactionInfo) {
        if (requiresNewTransaction(transactionInfo, transactionalAttribute)) {
            EntityManager entityManager = TransactionSynchronizationManager.createNewEntityManager(entityManagerFactory);
            return new TransactionInfo(transactionalAttribute, entityManager, transactionInfo, true);
        }

        return new TransactionInfo(transactionalAttribute, transactionInfo.getEntityManager(), transactionInfo, false);
    }

    private boolean requiresNewTransaction(TransactionInfo oldTransactionInfo, TransactionalAttribute transactionalAttribute) {
        Propagation propagation = transactionalAttribute.getPropagation();
        if (propagation.isNewTransactionRequired())
            return true;

        return oldTransactionInfo == null || !oldTransactionInfo.isOpenTransaction();
    }
}
