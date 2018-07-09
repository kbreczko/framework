package pl.insert.services;

import pl.insert.adnotations.Inject;
import pl.insert.adnotations.components.Service;
import pl.insert.adnotations.transactional.Transactional;
import pl.insert.models.UserDetails;
import pl.insert.repositories.UserDetailsRepository;
import pl.insert.transactional.TransactionalPropagation;

import javax.persistence.EntityManager;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Inject
    private EntityManager entityManager;

    @Inject
    private UserDetailsRepository userDetailsRepository;

    @Transactional(propagation = TransactionalPropagation.REQUIRED)
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
