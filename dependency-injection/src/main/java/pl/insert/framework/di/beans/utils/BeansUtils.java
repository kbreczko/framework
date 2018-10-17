package pl.insert.framework.di.beans.utils;

import pl.insert.framework.di.beans.BeanFactory;
import pl.insert.framework.di.exceptions.BeansException;

public class BeansUtils {

    public static <T> T getBean(BeanFactory beanFactory, Class<T> requiredType) {
        if (beanFactory == null)
            throw new BeansException("BeanFactory required for " + requiredType.getName() + " bean lookup");

        return beanFactory.getBean(requiredType);
    }
}
