package com.wuyiccc.hellodfs.admin.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author wuyiccc
 * @date 2021/2/13 21:00
 * 服务日志切面
 */
@Aspect
@Component
public class ServiceLogAspect {

    private Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * 监视项目各模块service包下代码执行的时间
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.wuyiccc.hellodfs.admin.*.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("======开始执行 {},{}========", joinPoint.getTarget().getClass(), joinPoint.getSignature().getName());
        long begin = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        long takeTime = end - begin;

        if (takeTime > 3000) {
            logger.error("==========执行结束, 耗时:{}毫秒==========", takeTime);
        } else if (takeTime > 2000) {
            logger.warn("=====执行结束，耗时：{}毫秒", takeTime);
        } else {
            logger.info("=========执行结束：耗时：{}毫秒==========", takeTime);
        }
        return result;
    }

}
