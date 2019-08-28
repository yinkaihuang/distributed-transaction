package cn.bucheng.rm.aspect;

import cn.bucheng.rm.holder.ConnectionProxyHolder;
import cn.bucheng.rm.holder.XidContext;
import cn.bucheng.rm.proxy.ConnectionProxy;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
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
public class DataSourceAspect {


    public static final int TIMEOUT = 1000 * 60 * 10;

    @Around("execution(* *.*..getConnection(..))")
    public Object aroundConnection(ProceedingJoinPoint point) throws Throwable {
        if (!XidContext.existXid())
            return point.proceed();
        Object result = point.proceed();
        if (!(result instanceof Connection))
            return result;
        log.info(" proxy db connection  with key:{}", XidContext.getXid());
        Connection connection = (Connection) result;
        connection.setAutoCommit(false);
        ConnectionProxy connectionProxy = new ConnectionProxy(connection);
        ConnectionProxyHolder.putProxy(XidContext.getXid(), connectionProxy, TIMEOUT);
        return connectionProxy;
    }
}
