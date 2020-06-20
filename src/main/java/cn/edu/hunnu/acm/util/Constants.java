package cn.edu.hunnu.acm.util;

import org.apache.ibatis.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constants {
    public static String avatar_absolute_path = null;
    public static String avatar_relative_path = null;
    public static String image_absolute_path = null;
    public static String image_relative_path = null;
    public static String secure_token = null;

    static {
        Properties prop = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream("backend.properties");
            prop.load(inputStream);
            avatar_absolute_path = prop.getProperty("avatar_absolute_path");
            avatar_relative_path = prop.getProperty("avatar_relative_path");
            image_absolute_path = prop.getProperty("image_absolute_path");
            image_relative_path = prop.getProperty("image_relative_path");
            secure_token = prop.getProperty("secure_token");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class userType {
        public static final String Regular = "Regular";
        public static final String Admin = "Admin";
        public static final String SuperAdmin = "SuperAdmin";
    }

    public static class myStatus {
        public static final short notTry = 0;
        public static final short success = 1;
        public static final short failed = 2;
    }

    public static class errorMessage {
        public static final String invalid_parameter = "参数非法";
        public static final String with_out_login = "用户没有登录";
        public static final String invalid_session = "session非法";
        public static final String have_not_permission = "用户不具有该权限";
        public static final String internal_error = "服务器内部错误";
        public static final String disk_access_error = "磁盘读写失败";
        public static final String password_does_not_match = "账号或者密码错误";
        public static final String email_does_not_match = "用户名和邮箱名不匹配";
        public static final String user_already_exist = "用户id已存在";
        public static final String user_not_found = "找不到该用户";
        public static final String problem_not_found = "找不到该题目";
        public static final String contest_not_found = "找不到该比赛";
        public static final String contest_not_start = "比赛尚未开始";
        public static final String submission_not_found = "找不到该提交";
        public static final String news_not_found = "找不到该新闻";
        public static final String action_not_support = "暂不支持该操作";
        public static final String file_size_exceed = "文件大小超出限制范围";
    }

    public static class pagination {
        public static final int rankLimit = 100;
        public static final int newsLimit = 10;
        public static final int userLimit = 30;
        public static final int discussLimit = 30;
        public static final int problemLimit = 30;
        public static final int contestLimit = 10;
        public static final int submissionLimit = 30;
        public static final int summaryList = 30;
    }

    public static class result {
        public static final short Pending = 0;
        public static final short Judging = 1;
        public static final short Accepted = 2;
        public static final short PresentationError = 3;
        public static final short WrongAnswer = 4;
        public static final short RuntimeError = 5;
        public static final short TimeLimitExceeded = 6;
        public static final short MemoryLimitExceeded = 7;
        public static final short OutputLimitExceeded  = 8;
        public static final short CompileError = 9;
        public static final short SystemError = 10;
        public static final short Frozen = -1;
    }

    public static class result_range {
        public static final short LRange = 0;
        public static final short RRange = 10;
    }

    public static class language {
        public static final short LANG_C = 0;
        public static final short LANG_CPP = 1;
        public static final short LANG_JAVA = 2;
    }

    public static class language_range {
        public static final short LRange = 0;
        public static final short RRange = 2;
    }

    public static class contestType {
        public static final short PUBLIC = 0;
        public static final short PRIVATE = 1;
    }

    public static class contestStatus {
        public static final short Pending = 0;
        public static final short Running = 1;
        public static final short Ending = 2;
    }

    public static class contest_range {
        public static final short LRange = 0;
        public static final short RRange = 1;
    }

    public static class announcementType {
        public static final short ContestType = 0;
        public static final short CarouselType = 1;
        public static final short ReportType = 2;
        public static final short SolutionType = 3;

        public static final String ReportName = "新闻报道";
        public static final String SolutionName = "解题报告";
    }

    public static class announcement_range {
        public static final short LRange = 0;
        public static final short RRange = 3;
    }
}
