package cn.edu.hunnu.acm.util;

import com.alibaba.fastjson.JSONObject;

public class DataMap {
    private JSONObject data;
    private Boolean success;
    private String err_info;

    public DataMap() {
        this.data = new JSONObject();
        this.success = true;
    }

    public DataMap success() {
        this.success = true;
        return this;
    }

    public DataMap fail() {
        this.success = false;
        return this;
    }

    public void setErrorInfo(String info) {
        this.err_info = info;
    }

    public Object get(String key) {
        return data.getString(key);
    }

    public void set(String key, Object value) {
        this.data.put(key, value);
    }

    public void remove(String key) {
        this.data.remove(key);
    }

    public void clear() {
        this.data.clear();
    }

    @Override
    public String toString() {
       if(success) {
           return String.format("{\"error\":false,\"data\":%s}", data.toJSONString());
       } else{
           return String.format("{\"error\":true,\"err_info\":\"%s\"}", err_info);
       }
    }
}
