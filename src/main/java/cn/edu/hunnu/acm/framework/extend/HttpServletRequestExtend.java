package cn.edu.hunnu.acm.framework.extend;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class HttpServletRequestExtend extends HttpServletRequestWrapper {
    private HttpServletRequest request;
    private ContentType contentType;
    private JSONObject data;

    enum ContentType {
        FORM, JSON, MULTIPART, TEXT, UNKNOWN
    }

    public HttpServletRequestExtend(HttpServletRequest request) throws IOException {
        super(request);
        this.init(request);
    }

    private void init(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();
        this.request = request;
        if(contentType.startsWith("application/x-www-form-urlencoded")) {
            this.contentType = ContentType.FORM;
        } else if(contentType.startsWith("application/json")) {
            this.contentType = ContentType.JSON;
            InputStream inputStream = request.getInputStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            this.data = JSONObject.parseObject(result.toString(StandardCharsets.UTF_8));
            result.close();
        } else if(contentType.startsWith("multipart/form-data")) {
            this.contentType = ContentType.MULTIPART;
        } else if(contentType.startsWith("text/plain")) {
            this.contentType = ContentType.TEXT;
        } else {
            this.contentType = ContentType.UNKNOWN;
        }
    }

    public String getParameter(String name) {
        String value = null;
        switch (this.contentType) {
            case FORM:
                value = request.getParameter(name);
                break;
            case JSON:
                value = data.getString(name);
                break;
            case MULTIPART:
            case TEXT:
            case UNKNOWN:
                break;
            default:
        }
        return value;
    }
}
