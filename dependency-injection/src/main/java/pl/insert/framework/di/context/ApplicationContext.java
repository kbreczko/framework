package pl.insert.framework.di.context;

public interface ApplicationContext {
    void close();

    <T> T getBean(Class<T> requiredType);
}
