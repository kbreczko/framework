package pl.insert.framework.entitymanager;

import javax.persistence.EntityManager;
import java.util.function.Supplier;

public interface EntityManagerUnit extends Supplier<EntityManager> {
    void set(EntityManager entityManager);

    EntityManager createNewEntityManager();
}
