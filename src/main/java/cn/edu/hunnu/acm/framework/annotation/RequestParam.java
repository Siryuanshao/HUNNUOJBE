package cn.edu.hunnu.acm.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface RequestParam {
    String value();
    String defaultValue() default "";
    boolean required() default true;
}
