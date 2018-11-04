package com.project.base.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheable {
    String[] cacheNames() default {};

    String key() default "";

    String keyGenerator() default "";

    int timeout() default 60;
}