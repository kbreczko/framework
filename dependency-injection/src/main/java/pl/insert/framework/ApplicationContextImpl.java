package pl.insert.framework;

import org.reflections.Reflections;
import pl.insert.framework.annotations.Bean;
import pl.insert.framework.annotations.ComponentScan;
import pl.insert.framework.annotations.Inject;
import pl.insert.framework.annotations.PersistenceContext;
import pl.insert.framework.annotations.components.Component;
import pl.insert.framework.annotations.components.Repository;
import pl.insert.framework.annotations.components.Service;
import pl.insert.framework.annotations.transactional.Transactional;
import pl.insert.framework.beans.DisposableBean;
import pl.insert.framework.entitymanager.EntityManagerHandler;
import pl.insert.framework.entitymanager.EntityManagerUnit;
import pl.insert.framework.entitymanager.EntityManagerUnitImpl;
import pl.insert.framework.proxy.DynamicProxyFactoryImpl;
import pl.insert.framework.transactional.TransactionInterceptor;
import pl.insert.framework.transactional.TransactionalHandler;
import pl.insert.framework.util.ClassUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationContextImpl implements ApplicationContext {
    private static String packageName = "pl.insert.framework";
    private static Map<Class, Class> components = new HashMap<>();

    static {
        scanComponents(packageName);
        components.put(EntityManager.class, EntityManagerUnitImpl.class);
    }
    private Map<Class, Object> applicationScope = new HashMap<>();
    private Optional<Object> appConfiguration = Optional.empty();
    private Map<Class, Method> beans = new HashMap<>();
    private List<DisposableBean> disposableBeans = new LinkedList<>();

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

    private static void scanComponents(String packageName) {
        Reflections reflections = new Reflections(packageName);

        Set<Class<?>> types = Stream.of(Service.class, Repository.class, Component.class)
                .flatMap(component -> reflections.getTypesAnnotatedWith(component).stream())
                .collect(Collectors.toSet());

        types.forEach(type -> {
            Arrays.stream(type.getInterfaces()).forEach(iface -> components.put(iface, type));
            components.put(type, type);
        });
    }

    @Override
    public void close() {
        disposableBeans.forEach(DisposableBean::destroy);
    }

    @Override
    public synchronized <T> T getBean(Class<T> clazz) {
        if (applicationScope.containsKey(clazz))
            return (T) applicationScope.get(clazz);

        Optional<Object> implementationClass = Optional.empty();

        if (clazz.isAssignableFrom(EntityManager.class) && beans.containsKey(EntityManagerFactory.class))
            implementationClass = Optional.of(createEntityManagerBean());
        else if (beans.containsKey(clazz) && appConfiguration.isPresent())
            implementationClass = Optional.of(createBeanFromConfiguration(clazz));
        else if (components.containsKey(clazz) && appConfiguration.isPresent())
            implementationClass = Optional.of(createBeanFromComponents(clazz));

        implementationClass.orElseThrow(() -> new NullPointerException("No bean named '" + clazz.getName() + "' is defined"));
        Object bean = implementationClass.get();
        injectDependencies(bean);
        bean = createProxy(clazz, bean);
        addToApplicationScope(clazz, bean);
        return (T) bean;
    }


    private Object createProxy(Class<?> clazz, Object bean) {
        Object proxy = bean;

        if (isTransactional(bean)) {
            TransactionInterceptor transactionInterceptor = getBean(TransactionInterceptor.class);
            proxy = new DynamicProxyFactoryImpl().createProxy(clazz, new TransactionalHandler(proxy, transactionInterceptor));
        }

        return proxy;
    }

    private boolean isTransactional(Object object) {
        boolean cLassTransactional = object.getClass().isAnnotationPresent(Transactional.class);
        boolean methodsTransactional = Arrays.stream(object.getClass().getMethods())
                .anyMatch(method -> method.isAnnotationPresent(Transactional.class));

        return cLassTransactional || methodsTransactional;
    }

    private Object createEntityManagerBean() {
        EntityManagerUnit entityManagerUnit = getBean(EntityManagerUnit.class);
        return new DynamicProxyFactoryImpl().createProxy(EntityManager.class, new EntityManagerHandler(entityManagerUnit));
    }

    private <T> Object createBeanFromComponents(Class<T> clazz) {
        Class component = components.get(clazz);
        return ClassUtil.newInstance(component);
    }

    private <T> Object createBeanFromConfiguration(Class<T> clazz) {
        Method method = beans.get(clazz);
        return ClassUtil.invokeMethod(appConfiguration.get(), method);
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

        if(object instanceof DisposableBean)
            disposableBeans.add((DisposableBean) object);
    }
}