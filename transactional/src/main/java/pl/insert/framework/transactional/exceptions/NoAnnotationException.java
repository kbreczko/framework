package pl.insert.framework.transactional.exceptions;

public class NoAnnotationException extends RuntimeException{
    public NoAnnotationException() {
    }

    public NoAnnotationException(String message) {
        super(message);
    }
}
