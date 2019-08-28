package cn.bucheng.rm.aspect;

import cn.bucheng.rm.constant.RemotingConstant;
import cn.bucheng.rm.holder.XidContext;
import cn.bucheng.rm.remoting.RemotingClient;
import cn.bucheng.rm.remoting.enu.CommandEnum;
import cn.bucheng.rm.remoting.protocol.RemotingCommand;
import cn.bucheng.rm.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author ：yinchong
 * @create ：2019/8/28 10:05
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
@Aspect
@Component
@Order(-Integer.MAX_VALUE)
public class GlobalTransactionalAspect {
    public static final int REGISTER_TIMEOUT = 1000 * 10;
    public static final int FIN_TIMEOUT = 1000 * 10;
    public static final int ERROR_TIMEOUT = 1000 * 10;
    @Autowired
    private RemotingClient client;

    public GlobalTransactionalAspect(RemotingClient client) {
        this.client = client;
    }

    @Around("@annotation(cn.bucheng.rm.annotation.GlobalTransactional)")
    public Object aroundGlobalTransactionalMethod(ProceedingJoinPoint point) throws Throwable {
        return runGlobalTransactionalMethod(point);
    }

//    @Around("this(cn.bucheng.rm.annotation.GlobalTransactional) && execution( * *(..))")
//    public Object aroundGlobalTransactionalMethodAll(ProceedingJoinPoint point) throws Throwable {
//        return runGlobalTransactionalMethod(point);
//    }

    @SuppressWarnings("all")
    private Object runGlobalTransactionalMethod(ProceedingJoinPoint point) throws Throwable {
        if (XidContext.existXid()) {
            return point.proceed();
        }
        String xid = WebUtils.getHeaderValue(RemotingConstant.REMOTING_REQUEST_HEADER);
        if (!Strings.isBlank(xid)) {
            XidContext.putXid(xid);
            RemotingCommand registerCommand = new RemotingCommand(xid, CommandEnum.REGISTER.getCode());
            client.invokeSync(registerCommand, REGISTER_TIMEOUT);
            try {
                return point.proceed();
            } catch (Throwable throwable) {
                log.error(throwable.toString());
                RemotingCommand errorCommand = new RemotingCommand(xid, CommandEnum.ERROR.getCode());
                client.invokeSync(errorCommand, ERROR_TIMEOUT);
                throw new RuntimeException(throwable);
            } finally {
                XidContext.removeXid();
            }
        }

        xid = XidContext.createAndSaveXid();
        RemotingCommand registerCommand = new RemotingCommand(xid, CommandEnum.REGISTER.getCode());
        client.invokeSync(registerCommand, REGISTER_TIMEOUT);
        try {
            return point.proceed();
        } catch (Throwable throwable) {
            log.error(throwable.toString());
            RemotingCommand errorCommand = new RemotingCommand(xid, CommandEnum.ERROR.getCode());
            client.invokeSync(errorCommand, ERROR_TIMEOUT);
            throw new RuntimeException(throwable);
        } finally {
            XidContext.removeXid();
            RemotingCommand finCommand = new RemotingCommand(xid, CommandEnum.FIN.getCode());
            client.invokeSync(finCommand, FIN_TIMEOUT);
        }
    }
}
