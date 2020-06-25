package cn.edu.hunnu.acm.framework;

import cn.edu.hunnu.acm.framework.annotation.*;
import cn.edu.hunnu.acm.framework.dispatcher.AbstractDispatcher;
import cn.edu.hunnu.acm.framework.dispatcher.GetDispatcher;
import cn.edu.hunnu.acm.framework.dispatcher.PostDispatcher;
import cn.edu.hunnu.acm.framework.dispatcher.FilterDispatcher;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;


public class DispatcherServlet extends HttpServlet {
    private Properties properties = new Properties();
    private List<String> classNames = new ArrayList<>();
    private Map<String, GetDispatcher> getMappings = new HashMap<>();
    private Map<String, PostDispatcher> postMappings = new HashMap<>();
    private Map<String, List<FilterDispatcher>> filterMappings = new HashMap<>();
    private List<Object> controllers = new ArrayList<>();
    private List<Object> interceptors = new ArrayList<>();
    private Map<String, Object> serviceFactory = new HashMap<>();
    private Map<String, MethodEntry> aroundMethod = new HashMap<>();

    private static class MethodEntry{
        Object instance;
        Method method;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        doScanner(properties.getProperty("scanPackage"));
        doInstance();
        doIoc();
        doAop();
        doHandlerMapping();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void doLoadConfig(String location) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(location.startsWith("classpath:")) {
            location = location.substring("classpath:".length());
        }
        InputStream resourceAsStream = cl.getResourceAsStream(location);
        try {
            if(resourceAsStream != null) {
                properties.load(resourceAsStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String packageName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = new File(Objects.requireNonNull(url).getFile());
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if(file.isDirectory()) {
                doScanner(packageName + "." + file.getName());
            } else {
                String className = packageName + "." + file.getName().replace(".class", "");
                classNames.add(className);
            }
        }
    }

    private void doInstance() {
        for(String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(Controller.class)) {
                    Object instance = clazz.getConstructor().newInstance();
                    controllers.add(instance);
                    System.err.printf("Add Controller: {%s}\n", clazz);
                } else if(clazz.isAnnotationPresent(Service.class)) {
                    Service service = clazz.getAnnotation(Service.class);
                    Object instance = clazz.getConstructor().newInstance();
                    serviceFactory.put(service.value(), instance);
                    System.err.printf("Add Service: {%s}\n", clazz);
                } else if(clazz.isAnnotationPresent(Aspect.class)) {
                    Object instance = clazz.getConstructor().newInstance();
                    interceptors.add(instance);
                    System.err.printf("Add Aspect: {%s}\n", clazz);
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    private void doIoc() {
        for(Object instance : controllers) {
            Class<?> clazz = instance.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields) {
                if(field.isAnnotationPresent(Qualifier.class)) {
                    Qualifier qualifier = field.getAnnotation(Qualifier.class);
                    String key = qualifier.value().isEmpty() ? field.getName() : qualifier.value();
                    field.setAccessible(true);
                    try {
                        field.set(instance, serviceFactory.get(key));
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void doAop() {
        for(Object instance : interceptors) {
            Class<?> clazz = instance.getClass();
            for(Method method : clazz.getMethods()) {
                if(method.isAnnotationPresent(Around.class)) {
                    Around mapping = method.getAnnotation(Around.class);
                    String simpleName = mapping.value();
                    MethodEntry entry = new MethodEntry();
                    entry.instance = instance;
                    entry.method = method;
                    aroundMethod.put(simpleName, entry);
                }
            }
        }
    }

    private void doHandlerMapping() throws ServletException {
        for(Object instance : controllers) {
            try {
                Class<?> clazz = instance.getClass();
                for(Method method : clazz.getMethods()) {
                    List<String> pathList = new ArrayList<>();
                    Annotation[] annotations = method.getAnnotations();
                    for(Annotation annotation : annotations) {
                        if(annotation instanceof GetMapping) {
                            GetMapping mapping = (GetMapping)annotation;
                            getMappings.put(mapping.value(), new GetDispatcher(instance, method, mapping.produces()));
                            System.err.printf("Found GET: {%s} => {%s}\n", mapping.value(), method);
                            pathList.add(mapping.value());
                        } else if(annotation instanceof PostMapping) {
                            PostMapping mapping = (PostMapping)annotation;
                            postMappings.put(mapping.value(), new PostDispatcher(instance, method, mapping.produces()));
                            System.err.printf("Found POST: {%s} => {%s}\n", mapping.value(), method);
                            pathList.add(mapping.value());
                        }
                    }
                    for(String path : pathList) {
                        List<FilterDispatcher> filterList = new ArrayList<>();
                        for(Annotation annotation : annotations) {
                            String simpleName = annotation.annotationType().getSimpleName();
                            MethodEntry entry = aroundMethod.get(simpleName);
                            if(entry != null) {
                                filterList.add(new FilterDispatcher(entry.instance, entry.method, annotation));
                            }
                        }
                        filterMappings.put(path, filterList);
                    }
                }
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String path = req.getRequestURI().substring(req.getContextPath().length());

        List<FilterDispatcher> filters = filterMappings.get(path);

        if(filters != null) {
            for(FilterDispatcher filter : filters) {
                try {
                    if(!filter.validate(req, resp)) {
                        return;
                    }
                } catch (Exception e) {
                    throw new ServletException(e);
                }
            }
        }

        AbstractDispatcher dispatcher = null;
        if(req.getMethod().equals("GET")) {
            dispatcher = getMappings.get(path);
        } else if(req.getMethod().equals("POST")) {
            dispatcher = postMappings.get(path);
        }
        if(dispatcher == null) {
            resp.sendError(404);
            return;
        }
        try {
            dispatcher.invoke(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

