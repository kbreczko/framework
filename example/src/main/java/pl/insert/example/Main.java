package pl.insert.example;

import pl.insert.example.models.UserDetails;
import pl.insert.example.services.UserDetailsService;
import pl.insert.framework.context.AnnotationConfigApplicationContext;
import pl.insert.framework.context.ApplicationContext;


public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
        UserDetailsService userDetailsService = context.getBean(UserDetailsService.class);
        UserDetails userDetails = new UserDetails("Name");
        userDetailsService.save(userDetails);
        context.close();
    }

}
