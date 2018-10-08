package pl.insert.framework.annotations.transactional;

import pl.insert.framework.transactional.enums.Propagation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transactional {
    Propagation propagation() default Propagation.REQUIRED;
}
