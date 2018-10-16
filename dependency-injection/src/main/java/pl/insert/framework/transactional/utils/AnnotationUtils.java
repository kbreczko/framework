package pl.insert.framework.transactional.utils;

import pl.insert.framework.transactional.exceptions.NoAnnotationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class AnnotationUtils {

    public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(clazz.getMethods()).anyMatch(method -> hasMethodWithAnnotation(method, annotationClass)) || isAnnotated(clazz, annotationClass);
    }

    private static boolean hasMethodWithAnnotation(Method method, Class<? extends Annotation> annotationClass){
        return method.isAnnotationPresent(annotationClass);
    }

    private static boolean isAnnotated(Class clazz, Class<? extends Annotation> annotationClass){
        return clazz.isAnnotationPresent(annotationClass);
    }

    public static <T extends Annotation> T extractAnnotation(Class<?> clazz, Method method, Class<T> annotationClass) throws NoSuchMethodException {
        if(hasMethodWithAnnotation(clazz, method, annotationClass))
            return clazz.getMethod(method.getName(), method.getParameterTypes()).getAnnotation(annotationClass);
        if(isAnnotated(clazz, annotationClass))
            return clazz.getAnnotation(annotationClass);

        throw new IllegalArgumentException("method is not transactional");
    }

    public static boolean hasMethodWithAnnotation(Class<?> clazz, Method abstractMethod, Class<? extends Annotation> annotationClass) throws NoSuchMethodException {
        return clazz.getMethod(abstractMethod.getName(), abstractMethod.getParameterTypes()).isAnnotationPresent(annotationClass);
    }

    public static boolean hasFieldWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass){
        return Arrays.stream(clazz.getDeclaredFields()).anyMatch(field -> field.isAnnotationPresent(annotationClass));
    }

    public static Field getFieldWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) throws NoAnnotationException {
        Optional<Field> first = Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(annotationClass))
            .findFirst();

        return first.orElseThrow(() -> new NoAnnotationException("field was not found with persistenceContext"));
    }
}
