package cn.edu.hunnu.acm.framework.dispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class FilterDispatcher {
    private Object instance;
    private Method method;
    private Annotation annotation;

    public FilterDispatcher(Object instance, Method method, Annotation annotation) {
        this.instance = instance;
        this.method = method;
        this.annotation = annotation;
    }

    public boolean validate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return (boolean)this.method.invoke(instance, new Object[]{request, response, annotation});
    }
}
