package com.project.base.redis.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheable {

    /**
     * 缓存key 的前缀
     *
     * @return
     */
    @AliasFor("cacheName")
    String value() default "";

    /**
     * 缓存key 的前缀
     *
     * @return
     */
    @AliasFor("value")
    String cacheName() default "";

    /**
     * 缓存key的后缀，支持el表达式
     *
     * @return
     */
    String key() default "";

    /**
     * 缓存key的后缀，自定义生成器
     *
     * @return
     */
    String keyGenerator() default "";

    /**
     * 缓存超时时间
     *
     * @return
     */
    int timeout() default 60;

    /**
     * redis flag选择器，选择哪个host进行操作
     *
     * @return
     */
    String flagExpression() default "";

    /**
     * cache name 与 key 的分隔符
     * @return
     */
    String splitter() default ":";
}
