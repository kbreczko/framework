package pl.insert.framework.beans.utils;

import pl.insert.framework.beans.BeanFactory;
import pl.insert.framework.exceptions.BeansException;

public class BeansUtils {

    public static <T> T getBean(BeanFactory beanFactory, Class<T> requiredType) {
        if (beanFactory == null)
            throw new BeansException("BeanFactory required for " + requiredType.getName() + " bean lookup");

        return beanFactory.getBean(requiredType);
    }
}
