package org.example.pivo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.pivo.annotation.Normalize;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Aspect
@Order(2)
@Component
@RequiredArgsConstructor
public class NormalizationAspect {

    private final ExpressionParser parser;

    @Pointcut("within(org.example.pivo.controller..*)")
    public void anyControllerMethod() {
    }

    @Before("anyControllerMethod()")
    public void normalizeArgs(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            normalizeRecursively(arg, new HashSet<>());
        }
    }

    private void normalizeRecursively(Object obj, Set<Object> visited) {
        if (obj == null || visited.contains(obj) || isSimple(obj.getClass())) {
            return;
        }
        visited.add(obj);

        for (Field field : getAllFields(obj.getClass())) {
            field.setAccessible(true);
            try {
                var value = field.get(obj);
                if (value == null) {
                    continue;
                }
                var newValue = normalizeFieldValue(field, value);
                if (newValue != value) {
                    field.set(obj, newValue);
                }
                normalizeRecursively(newValue, visited);
            } catch (Exception e) {
                log.warn("Failed to normalize {}.{}: {}",
                        obj.getClass().getSimpleName(),
                        field.getName(),
                        e.getMessage());
            }
        }
    }

    private Object normalizeFieldValue(Field field, Object value) {
        if (value instanceof String str) {
            return normalizeString(str, field);
        }
        return value;
    }

    private String normalizeString(String s, Field field) {
        var ann = field.getAnnotation(Normalize.class);
        if (ann == null) {
            return s.trim().replaceAll("\\s{2,}", " ");
        }
        try {
            var ctx = new StandardEvaluationContext(s);
            var expr = parser.parseExpression(ann.value());
            var result = expr.getValue(ctx, String.class);
            return result != null ? result : s;
        } catch (Exception e) {
            log.warn("SpEL error on {}: {}", field.getName(), e.getMessage());
            return s;
        }
    }

    private boolean isSimple(Class<?> type) {
        return type.isPrimitive() ||
                type == String.class ||
                Number.class.isAssignableFrom(type) ||
                type == Boolean.class ||
                type == Character.class ||
                type.isEnum();
    }

    private List<Field> getAllFields(Class<?> type) {
        var result = new ArrayList<Field>();
        while (type != null && type != Object.class) {
            result.addAll(List.of(type.getDeclaredFields()));
            type = type.getSuperclass();
        }
        return result;
    }
}
