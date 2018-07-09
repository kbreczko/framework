package pl.insert.framework.transactional;


public class TransactionalAttribute {
    private final TransactionalPropagation transactionalPropagation;

    public TransactionalAttribute(TransactionalPropagation transactionalPropagation) {
        this.transactionalPropagation = transactionalPropagation;
    }
}
