package pl.insert.entitymanager;

import javax.persistence.EntityManager;
import java.util.function.Supplier;

public interface EntityManagerUnit extends Supplier<EntityManager> {
    void set(EntityManager entityManager);

    EntityManager createNewEntityManager();
}
