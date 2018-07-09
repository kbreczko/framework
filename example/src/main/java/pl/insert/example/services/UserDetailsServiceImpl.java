package pl.insert.example.services;

import pl.insert.example.models.UserDetails;
import pl.insert.example.repositories.UserDetailsRepository;
import pl.insert.framework.adnotations.Inject;
import pl.insert.framework.adnotations.components.Service;

import javax.persistence.EntityManager;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Inject
    private EntityManager entityManager;

    @Inject
    private UserDetailsRepository userDetailsRepository;

    public void save(UserDetails userDetails) {
        entityManager.getTransaction().begin();
        userDetailsRepository.save(userDetails);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public UserDetails findById(long id) {
        entityManager.getTransaction().begin();
        UserDetails userDetails = userDetailsRepository.findById(id);
        entityManager.getTransaction().commit();
        entityManager.close();

        return userDetails;
    }
}
