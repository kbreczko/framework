package pl.insert.repositories;

import pl.insert.adnotations.Inject;
import pl.insert.adnotations.components.Repository;
import pl.insert.models.UserDetails;

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
