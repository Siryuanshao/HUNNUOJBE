package cn.edu.hunnu.acm.framework.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@SuppressWarnings("unused")
@Retention(RUNTIME)
@Target(METHOD)
@Inherited
public @interface Transactional {
    String value() default "";
}
