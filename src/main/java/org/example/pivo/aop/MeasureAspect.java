package org.example.pivo.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class MeasureAspect {
    private final ObjectMapper objectMapper;

    @Pointcut("@annotation(org.example.pivo.annotation.Measure)")
    public void annotatedWithMeasure(){}

    @Pointcut("within(org.example.pivo.controller..*)")
    public void anyControllerMethod(){}

    @Around("anyControllerMethod() || annotatedWithMeasure()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        var method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        var className = joinPoint.getTarget().getClass().getSimpleName();
        var methodName = method.getName();
        String argsJson;
        try{
            argsJson = objectMapper.writeValueAsString(joinPoint.getArgs());
        } catch (Exception e){
            argsJson = "unserializable args";
        }
        var startTime = System.currentTimeMillis();
        try{
            var result = joinPoint.proceed();
            var duration = System.currentTimeMillis() - startTime;
            log.info("@Measure: \n✓ {}#{} completed in {}ms with args: {}", className, methodName, duration, argsJson);
            return result;
        } catch (Throwable e){
            var duration = System.currentTimeMillis() - startTime;
            log.error("@Measure: \n✗ {}#{} failed in {}ms with args: {}",
                    className,
                    methodName,
                    duration,
                    argsJson,
                    e);
            throw e;
        }
    }
}
