package pl.insert.framework.transactional;

import javax.persistence.EntityManager;

public class TransactionInfo {
    private final TransactionalAttribute transactionalAttribute;
    private final EntityManager entityManager;
    private final TransactionInfo oldTransactionInfo;

    public TransactionInfo(TransactionalAttribute transactionalAttribute, EntityManager entityManager, TransactionInfo oldTransactionInfo) {
        this.transactionalAttribute = transactionalAttribute;
        this.entityManager = entityManager;
        this.oldTransactionInfo = oldTransactionInfo;
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
}
