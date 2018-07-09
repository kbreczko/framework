package pl.insert;

import pl.insert.adnotations.Bean;
import pl.insert.adnotations.ComponentScan;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ComponentScan("pl.insert")
public class AppConfiguration {
    private static final String persistanceUnitName = "database";
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistanceUnitName);

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return entityManagerFactory;
    }

}
