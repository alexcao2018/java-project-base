package com.project.base.model.validation;

import com.project.base.model.annotation.Description;
import com.project.base.model.validation.annotation.*;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Validation {

    /**
     *
     * @param object 验证对象
     * @param ignoreField 忽略字段
     * @param <T>
     * @return
     */
    public static <T> List<String> check(T object, String... ignoreField) {
        return check(object, null, Arrays.asList(ignoreField));
    }

    /**
     *
     * @param object 验证对象
     * @param functionValidation 自定义验证接口
     * @param <T>
     * @return
     */
    public static <T> List<String> check(T object, IValidation<T>... functionValidation) {
        return check(object, Arrays.asList(functionValidation), null);
    }

    /**
     *
     * @param object 验证对象
     * @param functionValidationCollection 自定义验证接口
     * @param ignoreFieldCollection 忽略字段
     * @param <T>
     * @return
     */
    public static <T> List<String> check(T object, List<IValidation<T>> functionValidationCollection, List<String> ignoreFieldCollection) {

        List<String> errorCollection = new ArrayList<>();

        if (functionValidationCollection != null && functionValidationCollection.size() > 0) {
            for (IValidation validation : functionValidationCollection) {
                String validateMessage = validation.validate(object);
                if (validateMessage != null)
                    errorCollection.add(validateMessage);
            }
        }


        Class<?> clazz = object.getClass();
        Field[] fieldCollection = clazz.getDeclaredFields();
        for (Field field : fieldCollection) {
            if (field.getDeclaredAnnotations().length == 0)
                continue;

            boolean isIgnore = ignoreFieldCollection != null && ignoreFieldCollection.stream().filter(x -> field.getName().equals(x)).count() > 0;
            if (isIgnore)
                continue;

            errorCollection.addAll(checkField(object, field));
        }
        return errorCollection;
    }

    private static List<String> checkField(Object object, Field field) {

        List<String> errorCollection = new ArrayList<>();
        String errorFormat = "{0} {1}";
        String fieldName = field.getName();

        Description descAnnotation = field.getAnnotation(Description.class);
        if (descAnnotation != null)
            fieldName = descAnnotation.value();


        Object fieldValue = null;
        try {
            field.setAccessible(true);
            fieldValue = field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Required requiredAnnotation = field.getAnnotation(Required.class);
        if (requiredAnnotation != null) {
            if (fieldValue == null || fieldValue.toString().matches(RegularExpression.Blank)) {
                errorCollection.add(requiredAnnotation.value());
                return errorCollection;
            }
        }

        if (fieldValue == null)
            return errorCollection;

        Decimal decimalAnnotation = field.getAnnotation(Decimal.class);
        if (decimalAnnotation != null) {
            if (fieldValue == null || !fieldValue.toString().matches(decimalAnnotation.pattern())) {
                errorCollection.add(decimalAnnotation.value());
            }
        }

        Email emailAnnotation = field.getAnnotation(Email.class);
        if (emailAnnotation != null) {
            if (fieldValue == null || !fieldValue.toString().matches(emailAnnotation.pattern())) {
                errorCollection.add(emailAnnotation.value());
            }
        }

        Mobile mobileAnnotation = field.getAnnotation(Mobile.class);
        if (mobileAnnotation != null) {
            if (fieldValue == null || !fieldValue.toString().matches(mobileAnnotation.pattern())) {
                errorCollection.add(mobileAnnotation.value());
            }
        }

        NaturalNumber naturalNumberAnnotation = field.getAnnotation(NaturalNumber.class);
        if (naturalNumberAnnotation != null) {
            if (fieldValue == null || !fieldValue.toString().matches(naturalNumberAnnotation.pattern())) {
                errorCollection.add(naturalNumberAnnotation.value());
            }
        }

        RegularExpression regularExpressionAnnotation = field.getAnnotation(RegularExpression.class);
        if (regularExpressionAnnotation != null) {
            if (fieldValue == null || !fieldValue.toString().matches(regularExpressionAnnotation.pattern())) {
                errorCollection.add(regularExpressionAnnotation.value());
            }
        }

        Tel telExpressionAnnotation = field.getAnnotation(Tel.class);
        if (telExpressionAnnotation != null) {
            if (fieldValue == null || !fieldValue.toString().matches(telExpressionAnnotation.pattern())) {
                errorCollection.add(telExpressionAnnotation.value());
            }
        }
        return errorCollection;

    }

}
