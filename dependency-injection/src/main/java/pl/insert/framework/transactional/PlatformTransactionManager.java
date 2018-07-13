package pl.insert.framework.transactional;


public interface PlatformTransactionManager {
    void open(TransactionInfo transactionInfo);

    void rollBack(TransactionInfo transactionInfo);

    void commit(TransactionInfo transactionInfo);

    void closeIfStillOpen(TransactionInfo transactionInfo);

    TransactionInfo createTransactionInfo(TransactionalAttribute transactionalAttribute, TransactionInfo transactionInfo);

    void restoreTransaction(TransactionInfo transactionInfo);
}
