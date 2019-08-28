package cn.bucheng.rm.feign;

import cn.bucheng.rm.constant.RemotingConstant;
import cn.bucheng.rm.holder.XidContext;
import cn.bucheng.rm.util.WebUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String key = XidContext.getXid();
        if (!Strings.isBlank(key)) {
            template.header(RemotingConstant.REMOTING_REQUEST_HEADER, key);
        }
        HttpServletRequest request = WebUtils.getRequest();
        if (request == null)
            return;
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null)
            return;
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = WebUtils.getRequest().getHeader(name);
            template.header(name, value);
        }
    }
}