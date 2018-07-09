package pl.insert.transactional;

import javax.persistence.EntityManager;

public interface PlatformTransactionManager {
    void open(TransactionInfo transactionInfo);

    void rollBack(TransactionInfo transactionInfo);

    void commit(TransactionInfo transactionInfo);

    void closeIfStillOpen(TransactionInfo transactionInfo);

    EntityManager newTransaction();
}
