package pl.insert.framework.transactional;

import pl.insert.framework.annotations.Inject;
import pl.insert.framework.annotations.components.Component;
import pl.insert.framework.entitymanager.EntityManagerUnit;

import javax.persistence.EntityManager;

@Component
public class PlatformTransactionManagerImpl implements PlatformTransactionManager {

    @Inject
    private EntityManagerUnit entityManagerUnit;

    @Override
    public EntityManager newTransaction() {
        return entityManagerUnit.createNewEntityManager();
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
}
