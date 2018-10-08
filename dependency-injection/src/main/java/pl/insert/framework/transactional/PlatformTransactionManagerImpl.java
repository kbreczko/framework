package pl.insert.framework.transactional;

import pl.insert.framework.entitymanager.EntityManagerUnit;
import pl.insert.framework.transactional.enums.Propagation;

import javax.persistence.EntityManager;
import java.util.List;


public class PlatformTransactionManagerImpl implements PlatformTransactionManager {
    private final EntityManagerUnit entityManagerUnit;
    private final List<Propagation> propagationWithNewTransaction = List.of(Propagation.REQUIRES_NEW);

    public PlatformTransactionManagerImpl(EntityManagerUnit entityManagerUnit) {
        this.entityManagerUnit = entityManagerUnit;
    }

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
        Propagation propagation = transactionalAttribute.getPropagation();
        if (propagationWithNewTransaction.stream().anyMatch(propagation::equals))
            return true;

        return oldTransactionInfo == null || !oldTransactionInfo.isOpenTransaction();
    }
}
