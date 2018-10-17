package pl.insert.framework.di.exceptions;

public class NoSuchBeanDefinitionException extends RuntimeException {
    public NoSuchBeanDefinitionException() {
    }

    public NoSuchBeanDefinitionException(String message) {
        super(message);
    }
}
