package com.chen.codegeneration.demo.druid;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class DSAspect {
    @Pointcut("@within(com.chen.codegeneration.demo.druid.DS) || @annotation(com.chen.codegeneration.demo.druid.DS)")
    public void dataSourcePointcut() {
    }

    @Before("dataSourcePointcut()")
    public void beforeSwitch(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DS methodDataSource = method.getAnnotation(DS.class);
        DSType value = null;
        if (methodDataSource != null) {
            value = methodDataSource.value();
        } else {
            Class<?> aClass = joinPoint.getTarget().getClass();
            DS annotation = aClass.getAnnotation(DS.class);
            if (annotation != null) {
                value = annotation.value();
            }
        }
        if (checkValue(value)) {
            DataSourceContextHolder.setDataSource(value);
        }
    }

    @SneakyThrows
    private boolean checkValue(final DSType value) {
        if (value != null) {
            boolean containKey = DynamicDataSource.getInstance().containKey(value);
            if (!containKey) {
                throw new Exception("数据源" + value + "未准备");
            }
        }
        return true;
    }

    @After("dataSourcePointcut()")
    public void afterSwitchDS() {
        DataSourceContextHolder.clear();
    }
}
