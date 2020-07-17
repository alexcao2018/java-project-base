package com.project.base.common.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectTool {
    /**
     * 修改final 字段属性值
     * @param object
     * @param field
     * @param newValue
     * @throws Exception
     */
    public static void setFinalFeildValue(Object object, Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(object, newValue);
    }
}
