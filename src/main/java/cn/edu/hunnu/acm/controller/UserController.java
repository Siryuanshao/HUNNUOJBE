package cn.edu.hunnu.acm.controller;

import cn.edu.hunnu.acm.aspect.annotation.LoginRequired;
import cn.edu.hunnu.acm.framework.annotation.*;
import cn.edu.hunnu.acm.model.User;
import cn.edu.hunnu.acm.service.EmailService;
import cn.edu.hunnu.acm.service.UserService;
import cn.edu.hunnu.acm.util.Constants;
import cn.edu.hunnu.acm.util.DataMap;
import cn.edu.hunnu.acm.util.TextUtils;
import cn.edu.hunnu.acm.util.ValidateChecker;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    @Qualifier
    private UserService userService;

    @Qualifier
    private EmailService emailService;

    @PostMapping(value = "/login", produces = "application/json;charset=utf-8")
    public DataMap login(HttpSession session,
                         @RequestParam("userId") String userId,
                         @RequestParam("password") String password) {
        User user = userService.checkLogin(userId, password);
        DataMap dataMap = new DataMap();
        if(user != null) {
            ServletContext context = session.getServletContext();
            HttpSession lastSession = (HttpSession) context.getAttribute("userId");
            if(lastSession != null) {
                lastSession.invalidate();
            }
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userType", user.getUserType());
            dataMap.set("userId", user.getUserId());
            dataMap.set("userName", user.getUserName());
            dataMap.set("userType", user.getUserType());
            dataMap.set("email", user.getEmail());
            dataMap.set("avatar", user.getAvatar() != null ? user.getAvatar() : TextUtils.pathJoin(Constants.avatar_relative_path, "default.png"));
            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.password_does_not_match);
            return dataMap.fail();
        }
    }

    @PostMapping(value = "/register", produces = "application/json;charset=utf-8")
    public DataMap register(@RequestParam("userId") String userId,
                            @RequestParam("userName") String userName,
                            @RequestParam("password") String password) {
        DataMap dataMap = new DataMap();
        if(!TextUtils.checkUserId(userId) || !TextUtils.checkUserName(userName) || !TextUtils.checkPassword(password)) {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
            return dataMap.fail();
        }

        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setPassword(password);

        userService.createUser(user);
        return dataMap.success();
    }

    @GetMapping(value = "/logout", produces = "application/json;charset=utf-8")
    public DataMap logout(HttpSession session) {
        session.invalidate();
        DataMap dataMap = new DataMap();
        return dataMap.success();
    }

    @LoginRequired
    @GetMapping(value = "/info", produces = "application/json;charset=utf-8")
    public DataMap getInfo(HttpSession session) {
        String userId = (String)session.getAttribute("userId");
        DataMap dataMap = new DataMap();
        if(userId == null) {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_session);
            return dataMap.fail();
        }
        User user = userService.queryUserById(userId);
        if(user != null) {
            dataMap.set("userId", user.getUserId());
            dataMap.set("userName", user.getUserName());
            dataMap.set("userType", user.getUserType());
            dataMap.set("email", user.getEmail());
            dataMap.set("avatar", user.getAvatar() != null ? user.getAvatar() : TextUtils.pathJoin(Constants.avatar_relative_path, "default.png"));
            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_session);
            return dataMap.fail();
        }
    }

    @GetMapping(value = "/checkUserIfExist", produces = "application/json;charset=utf-8")
    public DataMap checkUserIfExist(@RequestParam("userId") String userId) {
        boolean ifExist = userService.checkUserExist(userId);
        DataMap dataMap = new DataMap();

        if(!ifExist) {
            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.user_already_exist);
            return dataMap.fail();
        }
    }

    @GetMapping(value = "/profile", produces = "application/json;charset=utf-8")
    public DataMap getProfile(HttpSession session,
                              @RequestParam(value = "userId", required = false) String userId) {
        if(TextUtils.isEmpty(userId)) {
            userId = (String)session.getAttribute("userId");
        }
        DataMap dataMap = new DataMap();
        if(TextUtils.isEmpty(userId)) {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_session);
            return dataMap.fail();
        }
        Map<String, Object> profile = userService.getProfile(userId);
        User user = (User) profile.get("user");
        Integer rank = (Integer) profile.get("rank");
        List<Integer> solveList = (List<Integer>) profile.get("solveList");
        List<Integer> attemptList = (List<Integer>) profile.get("attemptList");

        if(user != null) {
            dataMap.set("userId", user.getUserId());
            dataMap.set("userName", user.getUserName());
            dataMap.set("createTime", user.getCreateTime());
            dataMap.set("lastLogin", user.getLastLogin());
            dataMap.set("school", user.getSchool());
            dataMap.set("grade", user.getGrade());
            dataMap.set("email", user.getEmail());
            dataMap.set("avatar", user.getAvatar() != null ? user.getAvatar() : TextUtils.pathJoin(Constants.avatar_relative_path, "default.png"));
            dataMap.set("sex", user.getSex());
            dataMap.set("whatUp", user.getWhatUp());

            dataMap.set("rank", rank);
            dataMap.set("accept", solveList.size());
            dataMap.set("attempt", attemptList.size());
            dataMap.set("submit", user.getSubmit());

            dataMap.set("solveList", solveList);
            dataMap.set("attemptList", attemptList);

            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.user_not_found);
            return dataMap.fail();
        }
    }

    @LoginRequired
    @PostMapping(value = "/uploadAvatar", produces = "application/json;charset=utf-8")
    public DataMap updateAvatar(HttpServletRequest request) {
        DataMap dataMap = new DataMap();
        try {
            HttpSession session = request.getSession();
            Part part = request.getPart("image");


            String userId = (String) session.getAttribute("userId");

            if(part.getSize() > 1024 * 1024 / 2) {
                dataMap.setErrorInfo(Constants.errorMessage.file_size_exceed);
                return dataMap.fail();
            } else if(userId != null) {
                String fileName = String.format("%s.%s", TextUtils.getRandomString(10), "jpeg");
                String fullPath = TextUtils.pathJoin(Constants.avatar_absolute_path, fileName);

                part.write(fullPath);
                TextUtils.modifyPermission(fullPath);

                User user = new User();
                user.setUserId(userId);
                user.setAvatar(TextUtils.pathJoin(Constants.avatar_relative_path, fileName));

                userService.updateProfile(user);
                return dataMap.success();
            } else {
                dataMap.setErrorInfo(Constants.errorMessage.invalid_session);
                return dataMap.fail();
            }
        } catch (Exception e) {
            e.printStackTrace();
            dataMap.setErrorInfo(Constants.errorMessage.disk_access_error);
            return dataMap.fail();
        }
    }

    @PostMapping(value = "/forgetPassword", produces = "application/json;charset=utf-8")
    public DataMap forgetPassword(@RequestParam("userId") String userId,
                                  @RequestParam("email") String email) {
        User user = userService.checkEmail(userId, email);
        DataMap dataMap = new DataMap();

        if(user != null && user.getEmail() != null) {
            emailService.sendMailSimple(user);
            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.email_does_not_match);
            return dataMap.fail();
        }
    }

    @LoginRequired
    @PostMapping(value = "/updateProfile", produces = "application/json;charset=utf-8")
    public DataMap updateProfile(HttpSession session,
                                 @RequestParam(value = "userName", required = false) String userName,
                                 @RequestParam(value = "school", required = false) String school,
                                 @RequestParam(value = "grade", required = false) String grade,
                                 @RequestParam(value = "whatUp", required = false) String whatUp,
                                 @RequestParam(value = "sex", required = false) Short sex) {
        String userId = (String) session.getAttribute("userId");
        DataMap dataMap = new DataMap();

        if(!(TextUtils.isEmpty(userName) || TextUtils.checkUserName(userName)) ||
           !(TextUtils.isEmpty(school) || TextUtils.checkUtil(school,32)) ||
           !(TextUtils.isEmpty(grade) || TextUtils.checkUtil(grade,32) ||
           !(TextUtils.isEmpty(whatUp) || TextUtils.checkUtil(whatUp, 64)))) {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
            return dataMap.fail();
        }

        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setSchool(school);
        user.setGrade(grade);
        user.setWhatUp(whatUp);
        user.setSex(sex);

        if(ValidateChecker.checkUserValidate(user)) {
            userService.updateProfile(user);
            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_session);
            return dataMap.fail();
        }
    }

    @LoginRequired
    @PostMapping(value = "/updateEmail", produces = "application/json;charset=utf-8")
    public DataMap updateEmail(HttpSession session,
                               @RequestParam("currentPassword") String currentPassword,
                               @RequestParam("newEmail") String newEmail) {
        String userId = (String) session.getAttribute("userId");
        DataMap dataMap = new DataMap();

        if(!TextUtils.checkEmail(newEmail)) {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
            return dataMap.fail();
        }

        if(userId != null) {
            Boolean result = userService.updateEmail(userId, currentPassword, newEmail);
            if(result) {
                return dataMap.success();
            } else {
                dataMap.setErrorInfo(Constants.errorMessage.password_does_not_match);
                return dataMap.fail();
            }
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_session);
            return dataMap.fail();
        }
    }

    @LoginRequired
    @PostMapping(value = "/updatePassword", produces = "application/json;charset=utf-8")
    public DataMap updatePassword(HttpSession session,
                                  @RequestParam("oldPassword") String oldPassword,
                                  @RequestParam("newPassword") String newPassword) {
        String userId = (String) session.getAttribute("userId");
        DataMap dataMap = new DataMap();

        if(!TextUtils.checkPassword(newPassword)) {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
            return dataMap.fail();
        }

        if(userId != null) {
            Boolean result = userService.updatePassword(userId, oldPassword, newPassword);
            if(result) {
                return dataMap.success();
            } else {
                dataMap.setErrorInfo(Constants.errorMessage.password_does_not_match);
                return dataMap.fail();
            }
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_session);
            return dataMap.fail();
        }
    }
}
