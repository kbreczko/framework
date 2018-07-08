package pl.insert;

import pl.insert.models.UserDetails;
import pl.insert.services.UserDetailsService;

import javax.persistence.EntityManagerFactory;


public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContextImpl(AppConfiguration.class);
        UserDetailsService userDetailsService = context.getBean(UserDetailsService.class);
        UserDetails userDetails = new UserDetails("Name");
        userDetailsService.save(userDetails);
        context.getBean(EntityManagerFactory.class).close();
    }

}
