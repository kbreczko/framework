package pl.insert.framework.exceptions;

public class NoUniqueBeanDefinitionException extends RuntimeException{
    public NoUniqueBeanDefinitionException() {
    }

    public NoUniqueBeanDefinitionException(String message) {
        super(message);
    }
}
