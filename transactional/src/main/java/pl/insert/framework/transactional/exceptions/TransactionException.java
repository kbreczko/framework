package pl.insert.framework.transactional.exceptions;

public class TransactionException extends RuntimeException {
    public TransactionException() {
    }

    public TransactionException(String message) {
        super(message);
    }
}
