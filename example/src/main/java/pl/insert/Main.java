package pl.insert;

import pl.insert.models.UserDetails;
import pl.insert.services.UserDetailsService;
import pl.insert.services.UserDetailsServiceImpl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("database");
        UserDetailsService userDetailsService = new UserDetailsServiceImpl(entityManagerFactory);
        UserDetails userDetails = new UserDetails("Name");
        userDetailsService.save(userDetails);
        entityManagerFactory.close();
    }

}
