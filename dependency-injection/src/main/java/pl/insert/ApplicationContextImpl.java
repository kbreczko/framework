package pl.insert;

import org.reflections.Reflections;
import pl.insert.adnotations.Bean;
import pl.insert.adnotations.Inject;
import pl.insert.adnotations.components.Repository;
import pl.insert.adnotations.components.Service;
import pl.insert.entitymanager.EntityManagerHandler;
import pl.insert.entitymanager.EntityManagerHelper;
import pl.insert.util.ClassUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApplicationContextImpl implements ApplicationContext {

    private static Map<Class, Object> applicationScope = new HashMap<>();
    private static Map<Class, Class> components = new HashMap<>();

    static {
        Reflections reflections = new Reflections("");
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(Service.class);
        types.addAll(reflections.getTypesAnnotatedWith(Repository.class));

        for (Class<?> implementationClass : types) {
            for (Class iface : implementationClass.getInterfaces()) {
                components.put(iface, implementationClass);
            }
            components.put(implementationClass, implementationClass);
        }
    }

    public ApplicationContextImpl() {
    }

    public ApplicationContextImpl(Class<?> clazz) {
        Object configuration = ClassUtil.newInstance(clazz);
        Class<?> aClass = configuration.getClass();
        Arrays.stream(aClass.getMethods()).filter(method -> method.isAnnotationPresent(Bean.class)).forEach(method -> System.out.print(method.getDeclaredAnnotations()));

        Arrays.stream(configuration.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .forEach(method -> createBeansFormConfiguration(method, configuration));
    }

    private void createBeansFormConfiguration(Method method, Object configuration) {
        Class<?> returnType = method.getReturnType();

        if (returnType.isAssignableFrom(EntityManagerFactory.class)) {
            Object entityManagerProxy = getEntityManagerProxy(configuration, method);
            applicationScope.put(EntityManager.class, entityManagerProxy);
        }
        Object bean = ClassUtil.invokeMethod(configuration, method);
        injectDependencies(bean);
        addBeans(returnType, bean);
    }

    private Object getEntityManagerProxy(Object configuration, Method method) {
        EntityManagerFactory entityManagerFactory = (EntityManagerFactory) ClassUtil.invokeMethod(configuration, method);
        EntityManagerHelper entityManagerHelper = new EntityManagerHelper(entityManagerFactory);
        EntityManagerHandler entityManagerHandler = new EntityManagerHandler(entityManagerHelper);
        return Proxy.newProxyInstance(EntityManager.class.getClassLoader(), EntityManager.class.getInterfaces(), entityManagerHandler);
    }

    @Override
    public synchronized <T> T getBean(Class<T> clazz) {
        if (applicationScope.containsKey(clazz))
            return (T) applicationScope.get(clazz);

        if (components.containsKey(clazz))
            clazz = components.get(clazz);

        Object implementation = ClassUtil.newInstance(clazz);
        injectDependencies(implementation);
        addBeans(clazz, implementation);
        return (T) implementation;
    }

    private void injectDependencies(Object implementation) {
        Arrays.stream(implementation.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .forEach(field -> ClassUtil.setField(field, implementation, getBean(field.getType())));
    }

    private void addBeans(Class<?> type, Object object) {
        Arrays.stream(object.getClass().getInterfaces()).forEach(iface -> applicationScope.put(iface, object));
        applicationScope.put(type, object);
    }
}
