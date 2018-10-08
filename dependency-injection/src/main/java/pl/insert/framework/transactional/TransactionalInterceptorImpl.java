package pl.insert.framework.transactional;

import pl.insert.framework.annotations.transactional.Transactional;
import pl.insert.framework.transactional.enums.Propagation;

import java.lang.reflect.Method;

public class TransactionalInterceptorImpl implements TransactionalInterceptor {
    private final ThreadLocal<TransactionInfo> transactionInfoThreadLocal = new ThreadLocal<>();
    private final PlatformTransactionManager platformTransactionManager;

    public TransactionalInterceptorImpl(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    @Override
    public void before(Object target, Method method, Object[] args) throws NoSuchMethodException {
        TransactionalAttribute transactionalAttribute = createTransactionalAttribute(target, method);
        TransactionInfo transactionInfo = platformTransactionManager.createTransactionInfo(transactionalAttribute, transactionInfoThreadLocal.get());
        transactionInfoThreadLocal.set(transactionInfo);
        platformTransactionManager.open(transactionInfoThreadLocal.get());
    }

    private TransactionalAttribute createTransactionalAttribute(Object target, Method method) throws NoSuchMethodException {
        Transactional annotation = target.getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotation(Transactional.class);
        Propagation propagation = annotation.propagation();
        return new TransactionalAttribute(propagation);
    }

    @Override
    public void after(Object target, Method method, Object[] args) {
        platformTransactionManager.commit(transactionInfoThreadLocal.get());
    }

    @Override
    public void afterThrowing(Object target, Method method, Object[] args, Throwable throwable) {
        platformTransactionManager.rollBack(transactionInfoThreadLocal.get());
    }

    @Override
    public void afterFinally(Object target, Method method, Object[] args) {
        TransactionInfo transactionInfo = transactionInfoThreadLocal.get();

        platformTransactionManager.closeIfStillOpen(transactionInfo);
        transactionInfoThreadLocal.set(transactionInfo.getOldTransactionInfo());

        if (transactionInfoThreadLocal.get() != null)
            platformTransactionManager.restoreTransaction(transactionInfoThreadLocal.get());
    }
}