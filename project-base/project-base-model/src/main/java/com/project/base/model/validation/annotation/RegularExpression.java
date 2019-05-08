package com.project.base.model.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegularExpression {
    public static final String Email = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
    public static final String Mobile = "^(13[0-9]|14[579]|15[0-3,5-9]|17[0135678]|18[0-9])\\d{8}$";
    public static final String Tel = "^\\d{7,15}$";
    public static final String Decimal = "^([0-9]*|\\d*\\.\\d{1}?\\d*)$";
    public static final String NaturalNumber = "^[0-9]*$";
    public static final String Blank = "/^$|\\s+/";
    public static final String MobileEz = "^[\\d]{11}$";
    String PassWord = "^.{6,16}$";
    String SmsCode = "^[\\d]{6}$";

    String pattern() default "";

    String value() default "格式不正确";

}
