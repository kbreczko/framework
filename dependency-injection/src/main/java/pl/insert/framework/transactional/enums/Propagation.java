package pl.insert.framework.transactional.enums;

public enum Propagation {
    REQUIRED(false),
    REQUIRES_NEW(true);

    private boolean newTransactionRequired;

    Propagation(boolean newTransactionRequired) {
        this.newTransactionRequired = newTransactionRequired;
    }

    public boolean isNewTransactionRequired() {
        return newTransactionRequired;
    }
}
