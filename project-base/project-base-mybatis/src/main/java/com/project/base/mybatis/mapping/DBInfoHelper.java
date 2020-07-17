package com.project.base.mybatis.mapping;

import java.util.concurrent.ConcurrentHashMap;

public class DBInfoHelper {

    private static ConcurrentHashMap<Class, TableInfo> tableInfoHolder = new ConcurrentHashMap<>();
    private static Object synObject = new Object();

    public static TableInfo getTableInfo(Class clazz) throws NoSuchFieldException, IllegalAccessException {
        TableInfo tableInfo = tableInfoHolder.get(clazz);
        if (tableInfo == null) {
            synchronized (synObject) {
                tableInfo = tableInfoHolder.get(clazz);
                if (tableInfo != null)
                    return tableInfo;
                tableInfo = new TableInfo(clazz);
                tableInfoHolder.put(clazz,tableInfo);
            }
        }

        return tableInfo;
    }

}
