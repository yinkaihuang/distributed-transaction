package cn.bucheng.rm.aspect;

import cn.bucheng.rm.constant.RemotingConstant;
import cn.bucheng.rm.holder.ConnectionProxyHolder;
import cn.bucheng.rm.holder.XidContext;
import cn.bucheng.rm.proxy.ConnectionProxy;
import cn.bucheng.rm.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * @author ：yinchong
 * @create ：2019/8/28 9:49
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
@Component
@Aspect
public class DataSourceAspect {


    public static final int TIMEOUT = 1000 * 60 * 5;

    @Around("execution(* *.*..getConnection(..))")
    public Object aroundConnection(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        if (!(result instanceof Connection))
            return result;
        String xid = WebUtils.getHeaderValue(RemotingConstant.REMOTING_REQUEST_HEADER);
        if(Strings.isBlank(xid)){
            xid = XidContext.getXid();
        }
        if(Strings.isBlank(xid)){
            return point.proceed();
        }
        XidContext.putXid(xid);
        //判断是否已经代理过了,如果代理过，直接复用上次改造的数据连接对象
        ConnectionProxyHolder.ConnectionProxyDefinition proxyDefinition = ConnectionProxyHolder.get(xid);
        if (proxyDefinition != null) {
            return proxyDefinition.proxy;
        }
        if (!ConnectionProxy.available()) {
            log.error("this no available tm connection");
            throw new RuntimeException("this no available tm connection");
        }
        log.info(" proxy db connection  with key:{}", XidContext.getXid());
        Connection connection = (Connection) result;
        connection.setAutoCommit(false);
        ConnectionProxy connectionProxy = new ConnectionProxy(connection);
        ConnectionProxyHolder.putProxy(XidContext.getXid(), connectionProxy, TIMEOUT);
        return connectionProxy;
    }
}
