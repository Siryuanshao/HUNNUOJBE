package cn.edu.hunnu.acm.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@SuppressWarnings("unused")
@Retention(RUNTIME)
@Target(METHOD)
public @interface PostMapping {
    String value();
    String produces() default "";
}
