package com.codrshi.smart_itinerary_planner.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void controllerMethods() {}

    @Pointcut("@execution(public com.codrshi.smart_itinerary_planner.repository..*(..))")
    public void repositoryMethods() {}

    @Pointcut("@execution(public com.codrshi.smart_itinerary_planner.util.tool..*(..))")
    public void toolMethods() {}

    @Before("controllerMethods()")
    public void logBeforeControllerMethods(JoinPoint joinPoint) {
        log.debug("<<< REQUEST INTERCEPTED BY CONTROLLER {}>>>", getMethodName(joinPoint));
        log.debug("Arguments to {} = {}", getMethodName(joinPoint), joinPoint.getArgs());
    }

    @AfterReturning(value = "controllerMethods()", returning = "response")
    public void logAfterReturningControllerMethods(JoinPoint joinPoint, Object response) {
        log.debug("Response from {} with response = {}", getMethodName(joinPoint), response);
    }

    @AfterThrowing(value = "controllerMethods()", throwing = "ex")
    public void logAfterThrowingControllerMethods(JoinPoint joinPoint, Exception ex) {
        log.error("Exception in {} with exception = {}", getMethodName(joinPoint), ex.getMessage());
    }

    @After("controllerMethods()")
    public void logAfterControllerMethods(JoinPoint joinPoint) {
        log.debug("<<< REQUEST EXECUTED BY CONTROLLER {}>>>", getMethodName(joinPoint));
    }

    @Before("repositoryMethods()")
    public void logBeforeRepositoryMethods(JoinPoint joinPoint) {
        log.debug("Call to repository method {} with arguments = {}", getMethodName(joinPoint), joinPoint.getArgs());
    }

    @AfterReturning(value = "repositoryMethods()", returning = "response")
    public void logAfterReturningRepositoryMethods(JoinPoint joinPoint, Object response) {
        log.debug("Response from repository method {} with response = {}", getMethodName(joinPoint), response);
    }

    @AfterThrowing(value = "repositoryMethods()", throwing = "ex")
    public void logAfterThrowingRepositoryMethods(JoinPoint joinPoint, Exception ex) {
        log.error("Exception in repository method {} with exception = {}", getMethodName(joinPoint), ex.getMessage());
    }

    @Before("toolMethods()")
    public void logBeforeToolMethods(JoinPoint joinPoint) {
        log.debug("Tool {} invoked with arguments = {}", getMethodName(joinPoint), joinPoint.getArgs());
    }

    @AfterReturning(value = "toolMethods()", returning = "response")
    public void logAfterReturningToolMethods(JoinPoint joinPoint, Object response) {
        log.debug("Response from tool {} = {}", getMethodName(joinPoint), response);
    }

    @AfterThrowing(value = "toolMethods()", throwing = "ex")
    public void logAfterThrowingToolMethods(JoinPoint joinPoint, Exception ex) {
        log.error("Exception in tool {} with exception = {}", getMethodName(joinPoint), ex.getMessage());
    }

    @After("toolMethods()")
    public void logAfterToolMethods(JoinPoint joinPoint) {
        log.debug("Tool {} executed", getMethodName(joinPoint));
    }

    private String getMethodName(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringTypeName() + "." + signature.getName();
    }
}
