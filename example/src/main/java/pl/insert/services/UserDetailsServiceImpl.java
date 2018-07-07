package pl.insert.services;

import pl.insert.models.UserDetails;
import pl.insert.repositories.UserDetailsRepository;
import pl.insert.repositories.UserDetailsRepositoryImpl;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class UserDetailsServiceImpl implements UserDetailsService {
    private EntityManagerFactory entityManagerFactory;

    public UserDetailsServiceImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void save(UserDetails userDetails) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        UserDetailsRepository userDetailsRepository = new UserDetailsRepositoryImpl(entityManager);
        userDetailsRepository.save(userDetails);

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public UserDetails findById(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        UserDetailsRepository userDetailsRepository = new UserDetailsRepositoryImpl(entityManager);
        UserDetails userDetails = userDetailsRepository.findById(id);

        entityManager.getTransaction().commit();
        entityManager.close();

        return userDetails;
    }
}
