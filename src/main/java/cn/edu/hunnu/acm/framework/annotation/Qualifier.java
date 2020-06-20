package cn.edu.hunnu.acm.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@SuppressWarnings("unused")
@Retention(RUNTIME)
@Target(FIELD)
public @interface Qualifier {
    String value() default "";
}
