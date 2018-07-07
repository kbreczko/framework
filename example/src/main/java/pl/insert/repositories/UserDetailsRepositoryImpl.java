package pl.insert.repositories;

import pl.insert.models.UserDetails;

import javax.persistence.EntityManager;

public class UserDetailsRepositoryImpl implements UserDetailsRepository {
    private EntityManager entityManager;

    public UserDetailsRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(UserDetails userDetails) {
        entityManager.persist(userDetails);
    }

    public UserDetails findById(long id) {
        return entityManager.find(UserDetails.class, id);
    }
}
