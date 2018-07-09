package pl.insert;

import org.reflections.Reflections;
import pl.insert.adnotations.Bean;
import pl.insert.adnotations.ComponentScan;
import pl.insert.adnotations.Inject;
import pl.insert.adnotations.PersistenceContext;
import pl.insert.adnotations.components.Repository;
import pl.insert.adnotations.components.Service;
import pl.insert.entitymanager.EntityManagerHandler;
import pl.insert.entitymanager.EntityManagerHelper;
import pl.insert.util.ClassUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class ApplicationContextImpl implements ApplicationContext {
    private Map<Class, Class> components = new HashMap<>();
    private Map<Class, Object> applicationScope = new HashMap<>();
    private Map<Class, Method> beans = new HashMap<>();
    private Optional<Object> appConfiguration = Optional.empty();

    public ApplicationContextImpl() {
    }

    public ApplicationContextImpl(Class<?> appConfiguration) {
        this.appConfiguration = Optional.ofNullable(ClassUtil.newInstance(appConfiguration));

        if (appConfiguration.isAnnotationPresent(ComponentScan.class))
            scanComponents(appConfiguration.getAnnotation(ComponentScan.class).value());

        Arrays.stream(appConfiguration.getMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .forEach(method -> {
                    Arrays.stream(method.getReturnType().getInterfaces()).forEach(iface -> beans.put(iface, method));
                    beans.put(method.getReturnType(), method);
                });
    }

    private void scanComponents(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(Service.class);
        types.addAll(reflections.getTypesAnnotatedWith(Repository.class));

        types.forEach(type -> {
            Arrays.stream(type.getInterfaces()).forEach(iface -> components.put(iface, type));
            components.put(type, type);
        });
    }

    @Override
    public synchronized <T> T getBean(Class<T> clazz) {
        if (applicationScope.containsKey(clazz))
            return (T) applicationScope.get(clazz);

        Optional<Object> implementationClass = Optional.empty();

        if (clazz.isAssignableFrom(EntityManager.class) && beans.containsKey(EntityManagerFactory.class)) {
            implementationClass = Optional.of(createEntityManagerBean());
        } else if (beans.containsKey(clazz) && appConfiguration.isPresent()) {
            implementationClass = Optional.of(createBeanFromConfiguration(clazz));
        } else if (components.containsKey(clazz) && appConfiguration.isPresent()) {
            implementationClass = Optional.of(createBeanFromComponents(clazz));
        }

        implementationClass.orElseThrow(() -> new NullPointerException("No bean named '" + clazz.getName() + "' is defined"));
        Object bean = implementationClass.get();
        injectDependencies(bean);
        addToApplicationScope(clazz, bean);
        return (T) bean;
    }

    private <T> Object createBeanFromComponents(Class<T> clazz) {
        Class component = components.get(clazz);
        return ClassUtil.newInstance(component);
    }

    private <T> Object createBeanFromConfiguration(Class<T> clazz) {
        Method method = beans.get(clazz);
        return ClassUtil.invokeMethod(appConfiguration.get(), method);
    }

    private Object createEntityManagerBean() {
        Method method = beans.get(EntityManagerFactory.class);
        EntityManagerFactory entityManagerFactory = (EntityManagerFactory) ClassUtil.invokeMethod(appConfiguration.get(), method);
        EntityManagerHelper entityManagerHelper = new EntityManagerHelper(entityManagerFactory);
        EntityManagerHandler entityManagerHandler = new EntityManagerHandler(entityManagerHelper);
        return Proxy.newProxyInstance(EntityManager.class.getClassLoader(), new Class[]{EntityManager.class}, entityManagerHandler);
    }

    private void injectDependencies(Object implementation) {
        Arrays.stream(implementation.getClass().getDeclaredFields())
                .filter(field -> isDependencyInjectionAnnotation(field))
                .forEach(field -> ClassUtil.setField(field, implementation, getBean(field.getType())));
    }

    private boolean isDependencyInjectionAnnotation(Field field) {
        return field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(PersistenceContext.class);
    }


    private void addToApplicationScope(Class<?> type, Object object) {
        Arrays.stream(object.getClass().getInterfaces()).forEach(iface -> applicationScope.put(iface, object));
        applicationScope.put(type, object);
    }
}
