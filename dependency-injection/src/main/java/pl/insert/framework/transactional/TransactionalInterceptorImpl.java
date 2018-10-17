package pl.insert.framework.transactional;

import pl.insert.framework.di.beans.BeanFactory;
import pl.insert.framework.di.beans.utils.BeansUtils;
import pl.insert.framework.transactional.annotations.Transactional;
import pl.insert.framework.transactional.enums.Propagation;
import pl.insert.framework.transactional.exceptions.TransactionException;
import pl.insert.framework.transactional.models.TransactionInfo;
import pl.insert.framework.transactional.models.TransactionalAttribute;
import pl.insert.framework.transactional.utils.AnnotationUtils;
import pl.insert.framework.util.Lazy;

import java.lang.reflect.Method;

public class TransactionalInterceptorImpl implements TransactionalInterceptor {
    private static final ThreadLocal<TransactionInfo> transactionInfoThreadLocal = new ThreadLocal<>();
    private BeanFactory beanFactory;
    private Lazy<TransactionManager> transactionManagerLazy = new Lazy<>(() -> BeansUtils.getBean(beanFactory, TransactionManager.class));

    @Override
    public void before(Object target, Method method, Object[] args) {
        TransactionalAttribute transactionalAttribute = createTransactionalAttribute(target, method);
        TransactionInfo oldTransactionInfo = transactionInfoThreadLocal.get();
        TransactionInfo transactionInfo = transactionManagerLazy.getOrCompute().createTransactionInfo(transactionalAttribute, oldTransactionInfo);
        transactionInfoThreadLocal.set(transactionInfo);
        transactionManagerLazy.getOrCompute().open(transactionInfo);
    }

    private TransactionalAttribute createTransactionalAttribute(Object target, Method method) {
        Propagation propagation;
        try {
            propagation = AnnotationUtils.extractAnnotation(target.getClass(), method, Transactional.class).propagation();
        } catch (NoSuchMethodException e) {
            throw new TransactionException(e.getMessage());
        }

        return new TransactionalAttribute(propagation);
    }

    @Override
    public void after(Object target, Method method, Object[] args) {
        transactionManagerLazy.getOrCompute().commit(transactionInfoThreadLocal.get());
    }

    @Override
    public void afterThrowing(Object target, Method method, Object[] args, Throwable throwable) {
        transactionManagerLazy.getOrCompute().rollBack(transactionInfoThreadLocal.get());
    }

    @Override
    public void afterFinally(Object target, Method method, Object[] args) {
        TransactionInfo transactionInfo = transactionInfoThreadLocal.get();
        transactionManagerLazy.getOrCompute().closeIfStillOpen(transactionInfo);
        transactionInfoThreadLocal.set(transactionInfo.getOldTransactionInfo());

        if (transactionInfo.getOldTransactionInfo() != null)
            transactionManagerLazy.getOrCompute().restoreTransaction(transactionInfo.getOldTransactionInfo());
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}