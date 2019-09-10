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

/**拦截存在@GlobalTransactional的方法，通过Order来保证多个拦截器执行时当前切面最先执行
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

    /**
     * 拦截方法方法上面存在GlobalTransactional
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("@annotation(cn.bucheng.rm.annotation.GlobalTransactional)")
    public Object aroundGlobalTransactionalMethod(ProceedingJoinPoint point) throws Throwable {
        return runGlobalTransactionalMethod(point);
    }


    @SuppressWarnings("all")
    private Object runGlobalTransactionalMethod(ProceedingJoinPoint point) throws Throwable {
        if (XidContext.existXid()) {
            return point.proceed();
        }
        if (!client.channelActive()) {
            log.error("remoting tm is not active");
            throw new RuntimeException("remoting tm is not active");
        }
        String xid = WebUtils.getHeaderValue(RemotingConstant.REMOTING_REQUEST_HEADER);
        if (!Strings.isBlank(xid)) {
            try {
                XidContext.putXid(xid);
                RemotingCommand registerCommand = new RemotingCommand(xid, CommandEnum.REGISTER.getCode());
                client.invokeSync(registerCommand, REGISTER_TIMEOUT);
                return point.proceed();
            } catch (Throwable throwable) {
                log.error("get error from transactonal: " + throwable.toString());
                RemotingCommand errorCommand = new RemotingCommand(xid, CommandEnum.ERROR.getCode());
                client.invokeSync(errorCommand, ERROR_TIMEOUT);
                throw new RuntimeException(throwable);
            } finally {
                XidContext.removeXid();
            }
        }

        try {
            xid = XidContext.createAndSaveXid();
            RemotingCommand registerCommand = new RemotingCommand(xid, CommandEnum.REGISTER.getCode());
            client.invokeSync(registerCommand, REGISTER_TIMEOUT);
            return point.proceed();
        } catch (Throwable throwable) {
            log.error("get error from transactonal: " + throwable.toString());
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
