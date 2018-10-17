package pl.insert.framework.di.exceptions;

public class NoUniqueBeanDefinitionException extends RuntimeException{
    public NoUniqueBeanDefinitionException() {
    }

    public NoUniqueBeanDefinitionException(String message) {
        super(message);
    }
}
