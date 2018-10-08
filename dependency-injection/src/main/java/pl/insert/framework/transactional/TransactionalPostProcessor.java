package pl.insert.framework.transactional;

import pl.insert.framework.annotations.transactional.Transactional;
import pl.insert.framework.beans.BeanPostProcessor;
import pl.insert.framework.proxy.DynamicProxyFactory;
import pl.insert.framework.util.Lazy;

import java.util.Arrays;
import java.util.function.Supplier;

public class TransactionalPostProcessor implements BeanPostProcessor {
    private Lazy<TransactionalInterceptor> transactionalInterceptor;

    public TransactionalPostProcessor(Supplier<TransactionalInterceptor> supplier) {
        this.transactionalInterceptor = new Lazy<>(supplier);
    }

    @Override
    public <T> T postProcess(T bean, String beanName) {
        if (isTransactional(bean))
            bean = DynamicProxyFactory.createProxy(bean.getClass().getInterfaces(), new TransactionalHandler(bean, transactionalInterceptor.getOrCompute()));

        return bean;
    }

    private boolean isTransactional(Object bean) {
        boolean isClass = bean.getClass().isAnnotationPresent(Transactional.class);
        boolean isMethod = Arrays.stream(bean.getClass().getMethods()).anyMatch(method -> method.isAnnotationPresent(Transactional.class));

        return isClass || isMethod;
    }
}
