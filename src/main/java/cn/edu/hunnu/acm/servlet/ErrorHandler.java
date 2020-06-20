package cn.edu.hunnu.acm.servlet;

import cn.edu.hunnu.acm.util.Constants;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ErrorHandler extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processError(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processError(request, response);
    }

    private void processError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        response.setStatus(200);
        if(throwable != null) {
            throwable.printStackTrace();
            response.getWriter().print(String.format("{\"error\":true,\"err_info\":\"%s\"}", Constants.errorMessage.invalid_parameter));
        }
    }
}
