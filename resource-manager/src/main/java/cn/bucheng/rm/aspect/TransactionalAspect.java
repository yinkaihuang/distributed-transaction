package cn.bucheng.rm.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author ：yinchong
 * @create ：2019/8/28 10:07
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
@Aspect
public class TransactionalAspect {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object aroundTransactionalMethod(ProceedingJoinPoint point) {
        return null;
    }
}
