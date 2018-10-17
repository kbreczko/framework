package pl.insert.framework.transactional.models;


import pl.insert.framework.transactional.enums.Propagation;

public final class TransactionalAttribute {
    private final Propagation propagation;

    public TransactionalAttribute(Propagation propagation) {
        this.propagation = propagation;
    }

    public Propagation getPropagation() {
        return propagation;
    }
}
