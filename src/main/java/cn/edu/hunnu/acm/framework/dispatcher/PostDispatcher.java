package cn.edu.hunnu.acm.framework.dispatcher;

import cn.edu.hunnu.acm.framework.annotation.RequestParam;
import cn.edu.hunnu.acm.framework.extend.HttpServletRequestExtend;
import cn.edu.hunnu.acm.framework.util.ParamParse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class PostDispatcher implements AbstractDispatcher {
    private Object instance;
    private Method method;
    private String produces;
    private String[] parameterNames;
    private Class<?>[] parameterClasses;
    private RequestParam[] annotations;

    public PostDispatcher(Object instance, Method method, String produces) {
        this.instance = instance;
        this.method = method;
        this.produces = produces;
        this.parameterNames = Arrays.stream(method.getParameters()).map(Parameter::getName).toArray(String[]::new);
        this.parameterClasses = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        annotations = new RequestParam[parameterClasses.length];
        int annotationIndex = 0;
        for(Annotation[] parameterAnnotation: parameterAnnotations) {
            for(Annotation annotation : parameterAnnotation) {
                if(annotation instanceof RequestParam) {
                    annotations[annotationIndex] = (RequestParam) annotation;
                    break;
                }
            }
            annotationIndex++;
        }
        Class<?> returnType = method.getReturnType();
        if(returnType.isPrimitive()) {
            throw new UnsupportedOperationException("Unexpected return type: " + returnType);
        }
    }

    @Override
    public void invoke(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequestExtend wrapper = new HttpServletRequestExtend(request);
        Object[] arguments = new Object[parameterClasses.length];
        for(int i = 0; i < parameterClasses.length; i++) {
            String parameterName = parameterNames[i];
            Class<?> parameterClass = parameterClasses[i];
            RequestParam annotation = annotations[i];
            String param = wrapper.getParameter(annotation != null ? annotation.value() : parameterName);
            if(parameterClass == HttpServletRequest.class) {
                arguments[i] = request;
            } else if(parameterClass == HttpServletResponse.class) {
                arguments[i] = response;
            } else if(parameterClass == HttpSession.class) {
                arguments[i] = request.getSession();
            } else if(parameterClass == Integer.class) {
                if(annotation != null) {
                    arguments[i] = ParamParse.parseIntWithAnnotation(param, annotation);
                } else {
                    arguments[i] = ParamParse.parseInt(param);
                }
            } else if(parameterClass == Long.class) {
                if(annotation != null) {
                    arguments[i] = ParamParse.parseLongWithAnnotation(param, annotation);
                } else {
                    arguments[i] = ParamParse.parseLong(param);
                }
            } else if(parameterClass == Short.class) {
                if(annotation != null) {
                    arguments[i] = ParamParse.parseShortWithAnnotation(param, annotation);
                } else {
                    arguments[i] = ParamParse.parseShort(param);
                }
            } else if(parameterClass == Boolean.class) {
                if(annotation != null) {
                    arguments[i] = ParamParse.parseBooleanWithAnnotation(param, annotation);
                } else {
                    arguments[i] = ParamParse.parseBoolean(param);
                }
            } else if(parameterClass == String.class) {
                if(annotation != null) {
                    arguments[i] = ParamParse.filterStringWithAnnotation(param, annotation);
                } else {
                    arguments[i] = ParamParse.filterString(param);
                }
            } else if(!parameterClass.isPrimitive()){
                arguments[i] = ParamParse.parsePOJO(wrapper, parameterClass);
            } else {
                throw new Exception("Missing handler for type: " + parameterClass);
            }
        }

        if(this.produces != null && produces.length() != 0) {
            response.setContentType(produces);
        }

        Object obj = this.method.invoke(instance, arguments);
        response.getWriter().print(obj.toString());
    }
}
