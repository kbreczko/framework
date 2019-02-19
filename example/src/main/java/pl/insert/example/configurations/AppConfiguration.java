package pl.insert.example.configurations;

import pl.insert.framework.di.annotations.Bean;
import pl.insert.framework.di.annotations.ComponentScan;
import pl.insert.framework.transactional.TransactionManager;
import pl.insert.framework.transactional.TransactionManagerImpl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ComponentScan("pl.insert")
public class AppConfiguration {
    private final static String persistenceUnitName = "database";
    private final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);

    @Bean
    public TransactionManager platformTransactionManager() {
        EntityManagerFactory entityManagerFactory = entityManagerFactory();
        return new TransactionManagerImpl(entityManagerFactory);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return entityManagerFactory;
    }

}
