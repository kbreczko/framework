package pl.insert.framework;

public interface ApplicationContext {
    <T> T getBean(Class<T> clazz);

    void close();
}
