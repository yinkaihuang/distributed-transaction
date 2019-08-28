package cn.bucheng.rm.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebUtils {

    private static ServletRequestAttributes getAttributes(){
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    private static String getRequestToken(){
        return getRequest().toString();
    }

    public static HttpServletRequest getRequest(){
        ServletRequestAttributes attributes = getAttributes();
        if(attributes==null)
            return null;
        return attributes.getRequest();
    }

    public static HttpServletResponse getResponse(){
        return getAttributes().getResponse();
    }
}