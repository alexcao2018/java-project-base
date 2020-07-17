package com.project.base.dubbo.annotation;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DubboBatchOption {

    /**
     * 分批count
     * @return
     */
    int batchCount() default 30;

    /**
     * 分批参数位置, 从 0 开始
     * @return
     */
    int batchParameterPosition();

    /**
     * 合并结果，默认false
     * @return
     */
    boolean mergeResult() default false;
}
