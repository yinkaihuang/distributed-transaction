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

/**拦截存在@Transactional的方法，通过Order来保证拦截相同目标方法时，其执行顺序低于@GlobalTransactional高于其他
 * @author ：yinchong
 * @create ：2019/8/28 10:07
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
@Aspect
@Order(-(Integer.MAX_VALUE - 1))
public class TransactionalAspect {
    public static final int REGISTER_TIMEOUT = 1000 * 10;
    public static final int ERROR_TIMEOUT = 1000 * 10;
    private RemotingClient client;

    public TransactionalAspect(RemotingClient client) {
        this.client = client;
    }

    /**
     * 拦截存在Transactional的方法
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object aroundTransactionalMethod(ProceedingJoinPoint point) throws Throwable {
        return runTransactionalMethod(point);
    }


    @Around("this(org.springframework.transaction.annotation.Transactional) && execution( * *(..))")
    public Object aroundWithTransactional(ProceedingJoinPoint joinPoint) throws Throwable {
        return runTransactionalMethod(joinPoint);
    }

    @SuppressWarnings("all")
    private Object runTransactionalMethod(ProceedingJoinPoint point) throws Throwable {
        if (XidContext.existXid())
            return point.proceed();
        String xid = WebUtils.getHeaderValue(RemotingConstant.REMOTING_REQUEST_HEADER);
        if (Strings.isBlank(xid))
            return point.proceed();
        if (!client.channelActive()) {
            log.error("remoting tm is not active");
            throw new RuntimeException("remoting tm is not active");
        }
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
}
