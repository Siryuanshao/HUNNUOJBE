package cn.edu.hunnu.acm.aspect;

import cn.edu.hunnu.acm.aspect.annotation.AdminRequired;
import cn.edu.hunnu.acm.aspect.annotation.LoginRequired;
import cn.edu.hunnu.acm.aspect.annotation.SuperAdminRequired;
import cn.edu.hunnu.acm.framework.annotation.Around;
import cn.edu.hunnu.acm.framework.annotation.Aspect;
import cn.edu.hunnu.acm.util.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Aspect
public class PrincipalAspect {
    @Around("LoginRequired")
    public boolean LoginRequired(HttpServletRequest request, HttpServletResponse response, LoginRequired loginRequired) throws IOException{
        if(request.getSession().getAttribute("userId") != null) {
            return true;
        } else {
            response.setContentType("application/json;charset=utf-8");
            String errMessage = String.format("{\"error\":true,\"err_info\":\"%s\"}", Constants.errorMessage.with_out_login);
            response.getWriter().print(errMessage);
            return false;
        }
    }

    @Around("AdminRequired")
    public boolean AdminRequired(HttpServletRequest request, HttpServletResponse response, AdminRequired adminRequired) throws IOException {
        String userType = (String) request.getSession().getAttribute("userType");
        if(Constants.userType.SuperAdmin.equals(userType) || Constants.userType.Admin.equals(userType)) {
            return true;
        } else {
            if(Constants.secure_token.equals(request.getHeader("secure_token"))) {
                return true;
            } else {
                response.setContentType("application/json;charset=utf-8");
                String errMessage = String.format("{\"error\":true,\"err_info\":\"%s\"}", Constants.errorMessage.have_not_permission);
                response.getWriter().print(errMessage);
                return false;
            }
        }
    }

    @Around("SuperAdminRequired")
    public boolean SuperAdminRequired(HttpServletRequest request, HttpServletResponse response, SuperAdminRequired SuperadminRequired) throws IOException {
        String userType = (String) request.getSession().getAttribute("userType");
        if(Constants.userType.SuperAdmin.equals(userType)) {
            return true;
        } else {
            response.setContentType("application/json;charset=utf-8");
            String errMessage = String.format("{\"error\":true,\"err_info\":\"%s\"}", Constants.errorMessage.have_not_permission);
            response.getWriter().print(errMessage);
            return false;
        }
    }
}
