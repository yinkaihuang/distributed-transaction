package cn.bucheng.rm.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebUtils {

    private static ServletRequestAttributes getAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }


    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = getAttributes();
        if (attributes == null)
            return null;
        return attributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        return getAttributes().getResponse();
    }

    /**
     * 获取请求头中内容
     * @param key
     * @return
     */
    public static String getHeaderValue(String key) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(key);
    }
}