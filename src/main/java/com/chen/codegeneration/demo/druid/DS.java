package com.chen.codegeneration.demo.druid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源注解
 *
 * @author CHE&N
 * @see DSAspect
 * @see DSType
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DS {
    DSType value() default DSType.MASTER;
}