package pl.insert.framework.transactional;

import pl.insert.framework.annotations.Inject;
import pl.insert.framework.annotations.components.Component;
import pl.insert.framework.entitymanager.EntityManagerUnit;

import javax.persistence.EntityManager;

@Component
public class PlatformTransactionManagerImpl implements PlatformTransactionManager {

    @Inject
    private EntityManagerUnit entityManagerUnit;

    @Override
    public void restoreTransaction(TransactionInfo transactionInfo) {
        entityManagerUnit.set(transactionInfo.getEntityManager());
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
        if (transactionInfo.isOpenTransaction() && transactionInfo.isNewTransaction())
            transactionInfo.getEntityManager().close();
    }

    @Override
    public TransactionInfo createTransactionInfo(TransactionalAttribute transactionalAttribute, TransactionInfo transactionInfo) {
        if (isNewTransaction(transactionInfo, transactionalAttribute)) {
            EntityManager entityManager = entityManagerUnit.createNewEntityManager();
            return new TransactionInfo(transactionalAttribute, entityManager, transactionInfo, true);
        }

        return new TransactionInfo(transactionalAttribute, transactionInfo.getEntityManager(), transactionInfo, false);
    }

    private boolean isNewTransaction(TransactionInfo oldTransactionInfo, TransactionalAttribute transactionalAttribute) {
        if (isRequiresNew(transactionalAttribute.getTransactionalPropagation()))
            return true;
        return oldTransactionInfo == null || !oldTransactionInfo.isOpenTransaction();
    }

    private boolean isRequiresNew(TransactionalPropagation propagation) {
        return propagation.equals(TransactionalPropagation.REQUIRES_NEW);
    }
}
