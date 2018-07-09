package pl.insert.transactional;

import pl.insert.adnotations.transactional.Transactional;
import pl.insert.proxy.AOPInterceptor;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;
import java.util.Optional;

public class TransactionInterceptor implements AOPInterceptor {
    private final ThreadLocal<TransactionInfo> transactionInfoThreadLocal = new ThreadLocal<>();
    private final PlatformTransactionManager platformTransactionManager;

    public TransactionInterceptor(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    @Override
    public void before(Method method, Object[] args) {
        Transactional annotation = method.getAnnotation(Transactional.class);
        TransactionalPropagation propagation = Optional.of(annotation.propagation()).orElse(TransactionalPropagation.REQUIRED);
        TransactionalAttribute transactionalAttribute = new TransactionalAttribute(propagation);
        Optional<TransactionInfo> transactionInfo = Optional.of(transactionInfoThreadLocal.get());

        if (isRequiresNew(propagation)) {
            createTransactionInfo(transactionalAttribute, transactionInfo.get());
        } else if (!transactionInfo.isPresent() || !transactionInfo.get().isOpenTransaction())
            createTransactionInfo(transactionalAttribute, transactionInfo.get());

        platformTransactionManager.open(transactionInfoThreadLocal.get());
    }

    private TransactionInfo createTransactionInfo(TransactionalAttribute transactionalAttribute, TransactionInfo oldTransactionInfo) {
        EntityManager entityManager = platformTransactionManager.newTransaction();
        TransactionInfo transactionInfo = new TransactionInfo(transactionalAttribute, entityManager, oldTransactionInfo);
        transactionInfoThreadLocal.set(transactionInfo);
        return transactionInfo;
    }

    @Override
    public void after(Method method, Object[] args) {
        platformTransactionManager.commit(transactionInfoThreadLocal.get());
    }

    @Override
    public void afterThrowing(Method method, Object[] args, Throwable throwable) {
        platformTransactionManager.rollBack(transactionInfoThreadLocal.get());
    }

    @Override
    public void afterFinally(Method method, Object[] args) {
        platformTransactionManager.closeIfStillOpen(transactionInfoThreadLocal.get());
        transactionInfoThreadLocal.set(transactionInfoThreadLocal.get().getOldTransactionInfo());
    }

    private boolean isRequiresNew(TransactionalPropagation propagation) {
        return propagation.equals(TransactionalPropagation.REQUIRES_NEW);
    }
}