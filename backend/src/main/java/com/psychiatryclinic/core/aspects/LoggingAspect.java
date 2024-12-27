package com.psychiatryclinic.core.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("within(com.psychiatryclinic.api.controllers..*)")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("==> Starting {} in {}.{} with args: {}", 
            joinPoint.getSignature().getName(), 
            className, 
            methodName,
            Arrays.toString(args));

        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            
            logger.info("<== Completed {}.{} in {} ms with result: {}", 
                className, 
                methodName, 
                elapsedTime,
                result);
            
            return result;
        } catch (Exception e) {
            logger.error("!!! Exception in {}.{}: {} - Stack trace: {}", 
                className, 
                methodName, 
                e.getMessage(),
                Arrays.toString(e.getStackTrace()));
            throw e;
        }
    }
} 