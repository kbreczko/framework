package pl.insert.framework.adnotations.transactional;

import pl.insert.framework.transactional.TransactionalPropagation;

public @interface Transactional {
    TransactionalPropagation propagation();
}
