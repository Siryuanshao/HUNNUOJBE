package cn.edu.hunnu.acm.service;

import cn.edu.hunnu.acm.model.User;

import java.util.Map;

public interface UserService {
    User queryUserById(String userId);
    User checkLogin(String userId, String password);
    User checkEmail(String userId, String email);
    boolean checkUserExist(String userId);
    Map<String, Object> getProfile(String userId);
    Map<String, Object> queryRankList(int offset, int limit);
    void createUser(User user);
    void updateProfile(User user);
    Boolean updatePassword(String userId, String oldPassword, String newPassword);
    Boolean updateEmail(String userId, String currentPassword, String newEmail);
    Boolean updateUserRole(String userId, String userType);
}
