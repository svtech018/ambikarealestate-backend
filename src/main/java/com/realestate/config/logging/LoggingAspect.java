package com.realestate.config.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.realestate.admin.serviceimpl..*(..)) || execution(* com.realestate.user.serviceimpl..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        logger.info("Entering {}.{}()", className, methodName);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            logger.info("Exiting {}.{}() - completed in {}ms", className, methodName, elapsed);
            return result;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            logger.error("Exception in {}.{}() after {}ms: {}", className, methodName, elapsed, e.getMessage());
            throw e;
        }
    }
}
