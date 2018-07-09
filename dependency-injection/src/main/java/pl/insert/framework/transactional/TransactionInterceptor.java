package pl.insert.framework.transactional;

import pl.insert.framework.annotations.Inject;
import pl.insert.framework.annotations.components.Component;
import pl.insert.framework.annotations.transactional.Transactional;
import pl.insert.framework.proxy.AOPInterceptor;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;
import java.util.Optional;

@Component
public class TransactionInterceptor implements AOPInterceptor {
    private final ThreadLocal<TransactionInfo> transactionInfoThreadLocal = new ThreadLocal<>();

    @Inject
    private PlatformTransactionManager platformTransactionManager;


    @Override
    public void before(Object target, Method method, Object[] args) throws NoSuchMethodException {
        TransactionalAttribute transactionalAttribute = createTransactionalAttribute(target, method);
        TransactionInfo transactionInfo = createTransactionInfo(transactionalAttribute);
        transactionInfoThreadLocal.set(transactionInfo);
        platformTransactionManager.open(transactionInfoThreadLocal.get());
    }

    private TransactionInfo createTransactionInfo(TransactionalAttribute transactionalAttribute) {
        if (isNewTransaction(Optional.ofNullable(transactionInfoThreadLocal.get()), transactionalAttribute)) {
            EntityManager entityManager = platformTransactionManager.newTransaction();
            return new TransactionInfo(transactionalAttribute, entityManager, transactionInfoThreadLocal.get(), true);
        }

        return new TransactionInfo(transactionalAttribute, transactionInfoThreadLocal.get().getEntityManager(), transactionInfoThreadLocal.get(), false);
    }

    private boolean isNewTransaction(Optional<TransactionInfo> oldTransactionInfo, TransactionalAttribute transactionalAttribute) {
        return isRequiresNew(transactionalAttribute.getTransactionalPropagation()) || !oldTransactionInfo.isPresent() || !oldTransactionInfo.get().isOpenTransaction();
    }

    private TransactionalAttribute createTransactionalAttribute(Object target, Method method) throws NoSuchMethodException {
        Transactional annotation = target.getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotation(Transactional.class);
        TransactionalPropagation propagation = annotation.propagation();
        return new TransactionalAttribute(propagation);
    }

    private boolean isRequiresNew(TransactionalPropagation propagation) {
        return propagation.equals(TransactionalPropagation.REQUIRES_NEW);
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
        platformTransactionManager.closeIfStillOpen(transactionInfoThreadLocal.get());
        transactionInfoThreadLocal.set(transactionInfoThreadLocal.get().getOldTransactionInfo());
    }
}