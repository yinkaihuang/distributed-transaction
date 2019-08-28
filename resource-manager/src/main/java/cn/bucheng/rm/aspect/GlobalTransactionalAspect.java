package cn.bucheng.rm.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
public class GlobalTransactionalAspect {

    @Around("@annotation(cn.bucheng.rm.annotation.GlobalTransactional)")
    public Object aroundGlobalTransactionalMethod(ProceedingJoinPoint point) {
        return null;
    }
}
