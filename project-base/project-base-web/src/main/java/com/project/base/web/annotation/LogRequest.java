package com.project.base.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用以下注解
 * @see com.project.base.model.annotation.LogRequest
 */
@Deprecated
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRequest {
    boolean value() default true;
}
