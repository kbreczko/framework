package pl.insert.framework.transactional;


import pl.insert.framework.transactional.models.TransactionInfo;
import pl.insert.framework.transactional.models.TransactionalAttribute;

public interface TransactionManager {
    void open(TransactionInfo transactionInfo);

    void rollBack(TransactionInfo transactionInfo);

    void commit(TransactionInfo transactionInfo);

    void closeIfStillOpen(TransactionInfo transactionInfo);

    TransactionInfo createTransactionInfo(TransactionalAttribute transactionalAttribute, TransactionInfo transactionInfo);

    void restoreTransaction(TransactionInfo transactionInfo);
}
