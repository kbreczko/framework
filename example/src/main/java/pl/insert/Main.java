package pl.insert;

import pl.insert.models.UserDetails;
import pl.insert.services.UserDetailsService;
import pl.insert.services.UserDetailsServiceImpl;

import javax.persistence.EntityManagerFactory;


public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContextImpl(AppConfiguration.class);
        EntityManagerFactory entityManagerFactory = context.getBean(EntityManagerFactory.class);
        UserDetailsService userDetailsService = new UserDetailsServiceImpl(entityManagerFactory);
        UserDetails userDetails = new UserDetails("Name");
        userDetailsService.save(userDetails);
        entityManagerFactory.close();
    }

}
