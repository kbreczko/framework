package pl.insert.framework.transactional;

import pl.insert.framework.di.beans.BeanFactory;
import pl.insert.framework.di.beans.config.BeanPostProcessor;
import pl.insert.framework.root.proxy.DynamicProxyFactory;
import pl.insert.framework.transactional.annotations.Transactional;
import pl.insert.framework.transactional.utils.AnnotationUtils;


public class TransactionalPostProcessor implements BeanPostProcessor {
    private BeanFactory beanFactory;

    @Override
    public <T> T postProcess(T bean, String beanName) {
        if (AnnotationUtils.hasAnnotation(bean.getClass(), Transactional.class))
            bean = DynamicProxyFactory.createProxy(bean.getClass().getInterfaces(), new TransactionalHandler(bean, createTransactionalInterceptor()));

        return bean;
    }

    private TransactionalInterceptor createTransactionalInterceptor() {
       TransactionalInterceptor transactionalInterceptor = new TransactionalInterceptorImpl();
       transactionalInterceptor.setBeanFactory(beanFactory);
       return transactionalInterceptor;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
