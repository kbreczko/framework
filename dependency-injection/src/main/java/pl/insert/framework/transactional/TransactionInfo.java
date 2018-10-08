package pl.insert.framework.transactional;

import javax.persistence.EntityManager;

public final class TransactionInfo {
    private final TransactionalAttribute transactionalAttribute;
    private final EntityManager entityManager;
    private final TransactionInfo oldTransactionInfo;
    private final boolean newTransaction;

    public TransactionInfo(TransactionalAttribute transactionalAttribute, EntityManager entityManager, TransactionInfo oldTransactionInfo, boolean newTransaction) {
        this.transactionalAttribute = transactionalAttribute;
        this.entityManager = entityManager;
        this.oldTransactionInfo = oldTransactionInfo;
        this.newTransaction = newTransaction;
    }

    public TransactionalAttribute getTransactionalAttribute() {
        return transactionalAttribute;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public TransactionInfo getOldTransactionInfo() {
        return oldTransactionInfo;
    }

    public boolean isOpenTransaction(){
        return entityManager.isOpen();
    }

    public boolean isActiveTransaction(){
        return entityManager.getTransaction().isActive();
    }

    public boolean isNewTransaction() {
        return newTransaction;
    }
}
