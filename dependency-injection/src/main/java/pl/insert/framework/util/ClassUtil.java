package pl.insert.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class ClassUtil {
    public static Optional invokeMethod(Object object, Method method) {
        try {
            return Optional.ofNullable(method.invoke(object));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static Optional newInstance(Class<?> clazz) {
        try {
            return Optional.of(clazz.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static void setField(Field field, Object obj, Object value) {
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Optional<Class<?>> forName(String name){
        try {
            return Optional.of(Class.forName(name));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
