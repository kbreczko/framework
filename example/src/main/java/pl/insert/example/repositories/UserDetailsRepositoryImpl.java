package pl.insert.example.repositories;

import pl.insert.example.models.UserDetails;
import pl.insert.framework.adnotations.Inject;
import pl.insert.framework.adnotations.components.Repository;

import javax.persistence.EntityManager;

@Repository
public class UserDetailsRepositoryImpl implements UserDetailsRepository {
    @Inject
    private EntityManager entityManager;

    public void save(UserDetails userDetails) {
        entityManager.persist(userDetails);
    }

    public UserDetails findById(long id) {
        return entityManager.find(UserDetails.class, id);
    }
}
