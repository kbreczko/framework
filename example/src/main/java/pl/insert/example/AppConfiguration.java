package pl.insert.example;

import pl.insert.framework.annotations.Bean;
import pl.insert.framework.annotations.ComponentScan;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ComponentScan("pl.insert")
public class AppConfiguration {
    private final static String persistenceUnitName = "database";

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return Persistence.createEntityManagerFactory(persistenceUnitName);
    }

}
