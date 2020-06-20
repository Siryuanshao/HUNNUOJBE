package cn.edu.hunnu.acm.serviceimpl;

import cn.edu.hunnu.acm.dao.SubmissionMapper;
import cn.edu.hunnu.acm.dao.UserMapper;
import cn.edu.hunnu.acm.framework.annotation.Service;
import cn.edu.hunnu.acm.model.User;
import cn.edu.hunnu.acm.service.UserService;
import cn.edu.hunnu.acm.util.AlgorithmUtils;
import cn.edu.hunnu.acm.util.GetSqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {
    public User queryUserById(String userId) {
        UserMapper userMapper = GetSqlSession.getSqlSession().getMapper(UserMapper.class);
        User user = null;
        try {
            user = userMapper.queryUserById(userId);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return user;
    }

    public User checkLogin(String userId, String password) {
        UserMapper userMapper = GetSqlSession.getSqlSession().getMapper(UserMapper.class);
        User user = null;
        try {
            user = userMapper.queryUserById(userId);
            if(user != null) {
                if(password.equals(user.getPassword())) {
                    userMapper.updateLastLogin(userId);
                } else {
                    user = null;
                }
            }
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return user;
    }

    public User checkEmail(String userId, String email) {
        User user = queryUserById(userId);
        if(user != null && !email.equals(user.getEmail())) return null;
        else return user;
    }

    public boolean checkUserExist(String userId) {
        return queryUserById(userId) != null;
    }

    public Map<String, Object> getProfile(String userId) {
        Map<String, Object> map = new HashMap<>();
        UserMapper userMapper = GetSqlSession.getSqlSession().getMapper(UserMapper.class);
        SubmissionMapper submissionMapper = GetSqlSession.getSqlSession().getMapper(SubmissionMapper.class);
        User user = null;
        try {
            user = userMapper.queryUserById(userId);
            Integer rank = userMapper.queryUserRank(userId);
            List<Integer> solveList = submissionMapper.queryUserSolvedList(userId);
            List<Integer> totalList = submissionMapper.queryUserTotalList(userId);
            List<Integer> attemptList = AlgorithmUtils.calculateDiff(solveList, totalList);
            GetSqlSession.commit();
            map.put("user", user);
            map.put("rank", rank);
            map.put("solveList", solveList);
            map.put("attemptList", attemptList);
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return map;
    }

    public Map<String, Object> queryRankList(int offset, int limit) {
        UserMapper userMapper = GetSqlSession.getSqlSession().getMapper(UserMapper.class);
        Map<String, Object> map = new HashMap<>();
        try {
            int userCount = 0;
            List<User> userList = null;
            userCount = userMapper.queryUserCount();
            userList = userMapper.queryRankList(offset, limit);
            GetSqlSession.commit();
            map.put("userCount", userCount);
            map.put("userList", userList);
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return map;
    }

    public void createUser(User user) {
        UserMapper userMapper = GetSqlSession.getSqlSession().getMapper(UserMapper.class);
        if(userMapper.queryUserById(user.getUserId()) == null) {
            try {
                userMapper.insertUser(user);
                GetSqlSession.commit();
            } catch (Exception e) {
                GetSqlSession.rollback();
                throw new RuntimeException(e);
            } finally {
                GetSqlSession.close();
            }
        }
    }

    public void updateProfile(User user) {
        UserMapper userMapper = GetSqlSession.getSqlSession().getMapper(UserMapper.class);
        try {
            userMapper.updateUserProfile(user);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
    }

    public Boolean updatePassword(String userId, String oldPassword, String newPassword) {
        return updateUserAuth(userId, oldPassword, newPassword, null);
    }

    public Boolean updateEmail(String userId, String currentPassword, String newEmail) {
        return updateUserAuth(userId, currentPassword, null, newEmail);
    }

    private Boolean updateUserAuth(String userId, String password, String newPassword, String newEmail) {
        UserMapper userMapper = GetSqlSession.getSqlSession().getMapper(UserMapper.class);
        Boolean updateOk = false;
        try {
            updateOk = userMapper.updateUserAuth(userId, password, newPassword, newEmail);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return updateOk;
    }

    public Boolean updateUserRole(String userId, String userType) {
        UserMapper userMapper = GetSqlSession.getSqlSession().getMapper(UserMapper.class);
        Boolean updateOk = false;
        try {
            updateOk = userMapper.updateUserRole(userId, userType);
            GetSqlSession.commit();
        } catch (Exception e){
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return updateOk;
    }
}
