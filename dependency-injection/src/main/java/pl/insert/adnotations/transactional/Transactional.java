package pl.insert.adnotations.transactional;

import pl.insert.transactional.TransactionalPropagation;

public @interface Transactional {
    TransactionalPropagation propagation();
}
