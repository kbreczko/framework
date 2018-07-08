package pl.insert;

public interface ApplicationContext {
    <T> T getBean(Class<T> clazz);
}
