package com.project.base.dubbo.annotation;


import com.project.base.dubbo.batch.BatchConfiguration;
import com.project.base.dubbo.batch.BatchRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({BatchRegistrar.class, BatchConfiguration.class})
public @interface EnableDubboBatchInvoke {
    String[] value() default "";

}
