package cn.edu.hunnu.acm.util;

import com.alibaba.fastjson.JSONArray;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TextUtils {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static String hashSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isAlphaOrDigit(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9');
    }

    public static String pathJoin(String dirName, String fileName) {
        if(dirName.charAt(dirName.length() - 1) == '/') return dirName + fileName;
        else return dirName + "/" + fileName;
    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public static void modifyPermission(String fileName) {
        File file = new File(fileName);
        file.setReadable(true, false);
    }

    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(hashSet.charAt(number));
        }
        return sb.toString();
    }

    public static boolean checkDateFormat(String date) {
        if(date == null) return false;
        try {
            LocalDateTime.parse(date, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkJSONArray(String text) {
        try {
            JSONArray.parseArray(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkUserId(String userId) {
        if(userId == null || userId.length() < 5 || userId.length() > 18) return false;
        int length = userId.length();
        for(int i = 0; i < length; i++) {
            if(!TextUtils.isAlphaOrDigit(userId.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkUserName(String userName) {
        if(userName == null || userName.length() < 2 || userName.length() > 30) return false;
        int length = userName.length();
        for(int i = 0; i < length; i++) {
            if(!Character.isLetterOrDigit(userName.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPassword(String password) {
        if(password == null || password.length() < 5 || password.length() > 30) return false;
        int length = password.length();
        for(int i = 0; i < length; i++) {
            if(!TextUtils.isAlphaOrDigit(password.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkEmail(String email) {
        return checkUtil(email, 32);
    }

    public static boolean checkUtil(String str, int length) {
        return str != null && str.length() < length;
    }

    public static String getFormatLocalDateTime() {
        return formatter.format(LocalDateTime.now());
    }

    public static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(obj);
            map.put(fieldName, value);
        }
        return map;
    }
}
