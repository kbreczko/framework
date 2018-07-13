package pl.insert.framework.entitymanager;

import pl.insert.framework.beans.DisposableBean;

import javax.persistence.EntityManager;
import java.util.function.Supplier;

public interface EntityManagerUnit extends Supplier<EntityManager>, DisposableBean {
    void set(EntityManager entityManager);

    EntityManager createNewEntityManager();
}
