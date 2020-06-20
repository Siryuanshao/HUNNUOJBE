package cn.edu.hunnu.acm.dao;

import cn.edu.hunnu.acm.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    User queryUserById(String userId);
    Integer queryUserRank(String userId);
    Integer queryUserCount();
    List<User> queryRankList(@Param("offset") int offset,
                             @Param("limit") int limit);
    void insertUser(User user);
    void updateLastLogin(String userId);
    void updateUserProfile(User user);
    Boolean updateUserAuth(@Param("userId") String userId,
                           @Param("password") String password,
                           @Param("newPassword") String newPassword,
                           @Param("newEmail") String newEmail);
    Boolean updateUserRole(@Param("userId") String userId,
                           @Param("userType") String userType);
    void updateUserSubmit(@Param("userId") String userId,
                          @Param("accept") Integer accept,
                          @Param("submit") Integer submit);
}
