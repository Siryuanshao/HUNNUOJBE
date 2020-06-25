package cn.edu.hunnu.acm.framework.util;

import cn.edu.hunnu.acm.framework.annotation.Alias;
import cn.edu.hunnu.acm.framework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ParamParse {
    public static Integer parseIntWithAnnotation(String str, RequestParam annotation) {
        String defaultValue = annotation.defaultValue();
        if(!annotation.defaultValue().isEmpty()) {
            return parseIntWithDef(str, defaultValue);
        } else if(annotation.required()){
            return parseIntRequire(str);
        } else {
            return parseInt(str);
        }
    }

    public static Integer parseInt(String str) {
        Integer ret = null;
        try{
            ret = Integer.parseInt(str);
        }catch (Exception ignored) {

        }
        return ret;
    }

    public static Integer parseIntRequire(String str) {
        return Integer.parseInt(str);
    }

    public static Integer parseIntWithDef(String str, String defaultValue) {
        Integer ret = parseInt(str);
        if(ret != null) return ret;
        else return Integer.parseInt(defaultValue);
    }

    public static Long parseLongWithAnnotation(String str, RequestParam annotation) {
        String defaultValue = annotation.defaultValue();
        if(!annotation.defaultValue().isEmpty()) {
            return parseLongWithDef(str, defaultValue);
        } else if(annotation.required()){
            return parseLongRequire(str);
        } else {
            return parseLong(str);
        }
    }

    public static Long parseLong(String str) {
        Long ret = null;
        try{
            ret = Long.parseLong(str);
        }catch (Exception ignored) {

        }
        return ret;
    }

    public static Long parseLongRequire(String str) {
        return Long.parseLong(str);
    }

    public static Long parseLongWithDef(String str, String defaultValue) {
        Long ret = parseLong(str);
        if(ret != null) return ret;
        else return Long.parseLong(defaultValue);
    }

    public static Short parseShortWithAnnotation(String str, RequestParam annotation) {
        String defaultValue = annotation.defaultValue();
        if(!annotation.defaultValue().isEmpty()) {
            return parseShortWithDef(str, defaultValue);
        } else if(annotation.required()){
            return parseShortRequire(str);
        } else {
            return parseShort(str);
        }
    }

    public static Short parseShort(String str) {
        Short ret = null;
        try{
            ret = Short.parseShort(str);
        }catch (Exception ignored) {

        }
        return ret;
    }

    public static Short parseShortRequire(String str) {
        return Short.parseShort(str);
    }

    public static Short parseShortWithDef(String str, String defaultValue) {
        Short ret = parseShort(str);
        if(ret != null) return ret;
        else return Short.parseShort(defaultValue);
    }

    public static Boolean parseBooleanWithAnnotation(String str, RequestParam annotation) {
        String defaultValue = annotation.defaultValue();
        if(!annotation.defaultValue().isEmpty()) {
            return parseBooleanWithDef(str, defaultValue);
        } else if(annotation.required()){
            return parseBooleanRequire(str);
        } else {
            return parseBoolean(str);
        }
    }

    public static Boolean parseBoolean(String str) {
        return str != null ? Boolean.parseBoolean(str) : null;
    }

    public static Boolean parseBooleanRequire(String str) {
        return Boolean.parseBoolean(str);
    }

    public static Boolean parseBooleanWithDef(String str, String defaultValue) {
        Boolean ret = parseBoolean(str);
        if(ret != null) return ret;
        else return Boolean.parseBoolean(defaultValue);
    }


    public static String filterStringWithAnnotation(String str, RequestParam annotation) {
        String defaultValue = annotation.defaultValue();
        if(!annotation.defaultValue().isEmpty()) {
            return filterStringWithDef(str, defaultValue);
        } else if(annotation.required()){
            return filterStringRequired(str);
        } else {
            return filterString(str);
        }
    }

    public static String filterString(String str) {
        return str;
    }

    public static String filterStringRequired(String str) {
        if(str == null) throw new RuntimeException("String cannot be null");
        return str;
    }

    public static String filterStringWithDef(String str, String defaultValue) {
        if(str == null || str.length() == 0) return defaultValue;
        return str;
    }

    public static Object parsePOJO(HttpServletRequest request, Class<?> parameterClass) throws Exception {
        Field[] fields = parameterClass.getDeclaredFields();
        Object instance = parameterClass.getConstructor().newInstance();
        for(Field field : fields) {
            String paramName = field.getName();
            Annotation[] annotations = field.getAnnotations();
            if(annotations != null) {
                for(Annotation annotation : annotations) {
                    if(annotation instanceof Alias) {
                        paramName = ((Alias) annotation).value();
                    }
                }
            }
            Class<?> fieldType = field.getType();
            String param = request.getParameter(paramName);
            field.setAccessible(true);

            if(fieldType == Integer.class) {
                field.set(instance, parseInt(param));
            } else if(fieldType == Long.class){
                field.set(instance, parseLong(param));
            } else if(fieldType == Short.class){
                field.set(instance, parseShort(param));
            } else if(fieldType == Boolean.class){
                field.set(instance, parseBoolean(param));
            } else if(fieldType == String.class) {
                field.set(instance, filterString(param));
            } else {
                throw new Exception("Missing handler for type: " + fieldType);
            }
        }
        return instance;
    }
}
