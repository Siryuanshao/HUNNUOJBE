package cn.edu.hunnu.acm.framework.dispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AbstractDispatcher {
    void invoke(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
