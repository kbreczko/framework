package pl.insert.framework.transactional.entitymanager;

import pl.insert.framework.di.beans.BeanFactory;
import pl.insert.framework.di.beans.config.BeanPostProcessor;
import pl.insert.framework.di.utils.BeansUtils;
import pl.insert.framework.root.proxy.DynamicProxyFactory;
import pl.insert.framework.root.util.ClassUtil;
import pl.insert.framework.transactional.annotations.PersistenceContext;
import pl.insert.framework.transactional.utils.AnnotationUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Field;

public class EntityManagerPostProcessor implements BeanPostProcessor {
    private BeanFactory beanFactory;

    @Override
    public Object postProcess(Object bean, String beanName) {
        Class<?> requiredType = bean.getClass();
        if(AnnotationUtils.hasFieldWithAnnotation(requiredType, PersistenceContext.class)){
            EntityManagerFactory entityManagerFactory = BeansUtils.getBean(beanFactory, EntityManagerFactory.class);
            EntityManagerHandler entityManagerHandler = new EntityManagerHandler(entityManagerFactory);
            EntityManager entityManager =  DynamicProxyFactory.createProxy(EntityManager.class, entityManagerHandler);
            Field fieldWithAnnotation = AnnotationUtils.getFieldWithAnnotation(requiredType, PersistenceContext.class);
            ClassUtil.setField(fieldWithAnnotation, bean, entityManager);
        }

        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
