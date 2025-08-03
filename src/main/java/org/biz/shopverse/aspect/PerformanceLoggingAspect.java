package org.biz.shopverse.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(PerformanceLoggingAspect.class);
    @Around("execution(* org.biz.shopverse.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        log.info("*^* {} excute time: {} ms", joinPoint.getSignature(), duration);

        return result;
    }
}
