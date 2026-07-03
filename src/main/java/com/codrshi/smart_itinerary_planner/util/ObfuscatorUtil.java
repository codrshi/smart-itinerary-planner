package com.codrshi.smart_itinerary_planner.util;

import com.codrshi.smart_itinerary_planner.common.Constant;
import com.codrshi.smart_itinerary_planner.util.annotation.Masked;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@Slf4j
@SuppressWarnings("future use")
public class ObfuscatorUtil {

    public static void obfuscate(Object... objects) {
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());

        for (Object object : objects) {
            try {
                scanNestedObject(object, visited);
            } catch (IllegalAccessException e) {
                log.error("obfuscation interrupted with error: ", e);
                return;
            }
            visited.clear();
        }
    }

    private static void scanNestedObject(Object object, Set<Object> visited) throws IllegalAccessException {
        if (object == null || visited.contains(object)) {
            return;
        }

        visited.add(object);
        Class<?> clazz = object.getClass();

        while (clazz != null && clazz != Object.class && !clazz.getName().startsWith("java.") &&
                !clazz.getName().startsWith("javax.")) {

            String classPath = clazz.getName();
            for (Field field : clazz.getDeclaredFields()) {

                field.setAccessible(true);
                if (field.isAnnotationPresent(Masked.class)) {
                    log.debug("@Masked detected on method's parameter field = {}", classPath + field.getName());
                    field.set(object, Constant.REDACTED);
                } else if (field.get(object) != null) {
                    scanNestedObject(field.get(object), visited);
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    public static Object[] obfuscateArgs(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs().clone();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] fieldNames = signature.getParameterNames();
        Annotation[][] annotations = signature.getMethod().getParameterAnnotations();

        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {
                if (annotation instanceof Masked) {
                    log.debug("@Masked detected on method parameter = {}", fieldNames[i]);
                    args[i] = Constant.REDACTED;
                }
            }
        }

        return args;
    }
}
