package com.codrshi.smart_itinerary_planner.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Order(3)
@Component
@Slf4j
public class LoggingAspect {

    private static final String CONTROLLER_POINTCUT = "@within(org.springframework.web.bind.annotation.RestController)";
    private static final String REPOSITORY_POINTCUT = "execution(* com.codrshi.smart_itinerary_planner.repository..*(..))";
    private static final String TOOL_POINTCUT = "execution(public com.codrshi.smart_itinerary_planner.util.tool..*(..))";

    @Before(CONTROLLER_POINTCUT)
    public void logBeforeControllerMethods(JoinPoint joinPoint) {
        log.debug("<<< REQUEST INTERCEPTED BY CONTROLLER {}>>>", getMethodName(joinPoint));
        log.debug("Arguments to {} = {}", getMethodName(joinPoint), joinPoint.getArgs());
    }

    @AfterReturning(value = CONTROLLER_POINTCUT, returning = "response")
    public void logAfterReturningControllerMethods(JoinPoint joinPoint, Object response) {
        log.debug("Response from {} with content = {}", getMethodName(joinPoint), response);
    }

    @AfterThrowing(value = CONTROLLER_POINTCUT, throwing = "ex")
    public void logAfterThrowingControllerMethods(JoinPoint joinPoint, Exception ex) {
        log.error("Exception in {} with message = {}", getMethodName(joinPoint), ex.getMessage());
    }

    @After(CONTROLLER_POINTCUT)
    public void logAfterControllerMethods(JoinPoint joinPoint) {
        log.debug("<<< REQUEST EXECUTED BY CONTROLLER {}>>>", getMethodName(joinPoint));
    }

    @Before(REPOSITORY_POINTCUT)
    public void logBeforeRepositoryMethods(JoinPoint joinPoint) {
        log.debug("Call to repository method {} with arguments = {}", getMethodName(joinPoint), joinPoint.getArgs());
    }

    @AfterReturning(value = REPOSITORY_POINTCUT, returning = "response")
    public void logAfterReturningRepositoryMethods(JoinPoint joinPoint, Object response) {
        log.debug("Response from repository method {} with content = {}", getMethodName(joinPoint), response);
    }

    @AfterThrowing(value = REPOSITORY_POINTCUT, throwing = "ex")
    public void logAfterThrowingRepositoryMethods(JoinPoint joinPoint, Exception ex) {
        log.error("Exception in repository method {} with message = {}", getMethodName(joinPoint), ex.getMessage());
    }

    @Before(TOOL_POINTCUT)
    public void logBeforeToolMethods(JoinPoint joinPoint) {
        log.debug("Tool {} invoked with arguments = {}", getMethodName(joinPoint), joinPoint.getArgs());
    }

    @AfterReturning(value = TOOL_POINTCUT, returning = "response")
    public void logAfterReturningToolMethods(JoinPoint joinPoint, Object response) {
        log.debug("Response from tool {} = {}", getMethodName(joinPoint), response);
    }

    @AfterThrowing(value = TOOL_POINTCUT, throwing = "ex")
    public void logAfterThrowingToolMethods(JoinPoint joinPoint, Exception ex) {
        log.error("Exception in tool {} with message = {}", getMethodName(joinPoint), ex.getMessage());
    }

    @After(TOOL_POINTCUT)
    public void logAfterToolMethods(JoinPoint joinPoint) {
        log.debug("Tool {} executed", getMethodName(joinPoint));
    }

    private String getMethodName(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringTypeName() + "." + signature.getName();
    }
}
