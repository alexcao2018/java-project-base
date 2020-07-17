package com.project.base.model.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SmsCode {
    String value() default "验证码输入错误！";

    String pattern() default RegularExpression.SmsCode;
}
