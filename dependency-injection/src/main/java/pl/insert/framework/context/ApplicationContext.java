package pl.insert.framework.context;

public interface ApplicationContext {
    void close();

    <T> T getBean(Class<T> requiredType);
}
