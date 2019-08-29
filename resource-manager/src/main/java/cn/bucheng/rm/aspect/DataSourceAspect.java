package cn.bucheng.rm.aspect;

import cn.bucheng.rm.holder.ConnectionProxyHolder;
import cn.bucheng.rm.holder.XidContext;
import cn.bucheng.rm.proxy.ConnectionProxy;
import cn.bucheng.rm.remoting.RemotingClient;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
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
@Order(Integer.MAX_VALUE)
public class DataSourceAspect {

    public static final int TIMEOUT = 1000 * 60 * 5;

    /**
     * 拦截从spring容器中获取连接并进行改造
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(java.sql.Connection *.*..getConnection(..))")
    public Object aroundConnection(ProceedingJoinPoint point) throws Throwable {
        //普通事务
        if (!XidContext.existXid()) {
            return point.proceed();
        }
        //判断是否已经代理过了,如果代理过，直接复用上次改造的数据连接对象
        ConnectionProxyHolder.ConnectionProxyDefinition proxyDefinition = ConnectionProxyHolder.get(XidContext.getXid());
        if (proxyDefinition != null) {
            return proxyDefinition.proxy;
        }
        if (!ConnectionProxy.available()) {
            log.error("this no available tm connection");
            throw new RuntimeException("this no available tm connection");
        }
        Object result = point.proceed();
        log.info(" proxy db connection  with key:{}", XidContext.getXid());
        Connection connection = (Connection) result;
        connection.setAutoCommit(false);
        ConnectionProxy connectionProxy = new ConnectionProxy(connection);
        ConnectionProxyHolder.putProxy(XidContext.getXid(), connectionProxy, TIMEOUT);
        return connectionProxy;
    }
}
