package pl.insert.framework.entitymanager;

import pl.insert.framework.beans.BeanPostProcessor;
import pl.insert.framework.proxy.DynamicProxyFactory;

import javax.persistence.EntityManager;
import java.util.function.Supplier;

public class EntityManagerPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcess(Object bean, String beanName) {
        if(bean instanceof EntityManagerUnit && beanName.equals("EntityManager"))
            return DynamicProxyFactory.createProxy(EntityManager.class, new EntityManagerHandler((Supplier<?>) bean));

        return bean;
    }
}
