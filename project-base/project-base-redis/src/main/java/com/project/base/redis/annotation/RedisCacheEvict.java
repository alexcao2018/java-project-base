package com.project.base.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheEvict {
    String[] cacheNames() default {};
    boolean allEntries() default false;
    String key() default "";
    String keyGenerator() default "";
}
