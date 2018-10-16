package pl.insert.framework.transactional.entitymanager;

import pl.insert.framework.annotations.PersistenceContext;
import pl.insert.framework.beans.BeanFactory;
import pl.insert.framework.beans.BeanPostProcessor;
import pl.insert.framework.beans.utils.BeansUtils;
import pl.insert.framework.proxy.DynamicProxyFactory;
import pl.insert.framework.transactional.utils.AnnotationUtils;
import pl.insert.framework.util.ClassUtil;

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
