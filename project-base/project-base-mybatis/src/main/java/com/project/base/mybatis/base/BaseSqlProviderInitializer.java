package com.project.base.mybatis.base;

import com.project.base.mybatis.init.ApplicationContextAware4Mybatis;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class BaseSqlProviderInitializer {

    private static XMLLanguageDriver languageDriver = new XMLLanguageDriver();

    public static String init() {
        ApplicationContext applicationContext = ApplicationContextAware4Mybatis.getContext();
        DefaultSqlSessionFactory defaultSqlSessionFactory = applicationContext.getBean(DefaultSqlSessionFactory.class);
        Configuration configuration = defaultSqlSessionFactory.getConfiguration();

        Object[] mappedStatementArray =  configuration.getMappedStatements().toArray();
        for (int i = 0; i < mappedStatementArray.length; i++) {
            if(!(mappedStatementArray[i] instanceof  MappedStatement))
                continue;
            MappedStatement mappedStatement = (MappedStatement) mappedStatementArray[i];

            if (mappedStatement.getSqlSource() instanceof ProviderSqlSource) {
                Class<?> providerClass = getProviderClass(mappedStatement);
                if (providerClass != BaseSelectProvider.class)
                    continue;

                if (!mappedStatement.getId().endsWith(".getById")
                        && !mappedStatement.getId().endsWith(".deleteById")
                        && !mappedStatement.getId().endsWith(".count")) {
                    continue;
                }

                Class<?> mapperClass = getMapperClass(mappedStatement);
                Class<?>[] generics = getMapperGenerics(mapperClass);
                Class<?> modelClass = generics[0];
                Class<?> primaryFieldClass = generics[1];
                ResultMap resultMap = getResultMap(mappedStatement, modelClass);
                String sqlScript = getSqlScript(mappedStatement, mapperClass, modelClass, primaryFieldClass, resultMap);
                SqlSource sqlSource = createSqlSource(mappedStatement, sqlScript);
                setSqlSource(mappedStatement, sqlSource);
            }
        }

        return "sql";
    }

    private static SqlSource createSqlSource(MappedStatement mappedStatement, String script) {
        return languageDriver.createSqlSource(mappedStatement.getConfiguration(), "<script>" + script + "</script>", null);
    }

    private static void setSqlSource(MappedStatement mappedStatement, SqlSource sqlSource) {
        MetaObject metaObject = SystemMetaObject.forObject(mappedStatement);
        metaObject.setValue("sqlSource", sqlSource);
    }

    private static Class<?> getProviderClass(MappedStatement mappedStatement) {
        try {
            Field providerTypeField = ProviderSqlSource.class.getDeclaredField("providerType");
            providerTypeField.setAccessible(true);
            Class<?> clazz = (Class<?>) providerTypeField.get(mappedStatement.getSqlSource());
            return clazz;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> getMapperClass(MappedStatement mappedStatement) {
        try {
            String mappedStatementId = mappedStatement.getId();
            String className = mappedStatementId.substring(0, mappedStatementId.lastIndexOf("."));
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getSqlScript(MappedStatement mappedStatement, Class<?> mapperClass, Class<?> modelClass, Class<?> primaryFieldClass, ResultMap resultMap) {
        try {
            Method builderMethod = getBuilderMethod(mappedStatement, BaseSelectProvider.class, MappedStatement.class, Class.class, Class.class, Class.class, ResultMap.class);
            return builderMethod.invoke(null, mappedStatement, mapperClass, modelClass, primaryFieldClass, resultMap).toString();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getBuilderMethod(MappedStatement mappedStatement, Class<?> builderClass, Class<?>... parameterTypes) {
        try {
            String mappedStatementId = mappedStatement.getId();
            String methodName = mappedStatementId.substring(mappedStatementId.lastIndexOf(".") + 1);
            return builderClass.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static ResultMap getResultMap(MappedStatement mappedStatement, Class<?> modelClass) {
        Configuration configuration = mappedStatement.getConfiguration();
        Object[] resultMapArray =  configuration.getResultMaps().toArray();
        for (int i = 0; i < resultMapArray.length; i++) {

            if (!(resultMapArray[i] instanceof ResultMap))
                continue;
            ResultMap resultMap = (ResultMap) resultMapArray[i];
            if (modelClass == resultMap.getType() && !resultMap.getId().contains("-"))
                return resultMap;
        }
        return null;
    }

    private static Class<?>[] getMapperGenerics(Class<?> mapperClass) {
        Type[] types = mapperClass.getGenericInterfaces();
        for (Type type : types) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (IBaseMapper.class != (Class<?>) parameterizedType.getRawType())
                continue;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            Class<?>[] generics = new Class[typeArguments.length];
            for (int i = 0; i < typeArguments.length; i++)
                generics[i] = (Class<?>) typeArguments[i];
            return generics;
        }
        return null;
    }

    private static String getTableName(Class<?> mapperClass, Class<?> modelClass, ResultMap resultMap) {
        if (resultMap != null)
            return resultMap.getId().substring(mapperClass.getName().length() + 1);
        return toUnderline(modelClass.getSimpleName());
    }

    private static String getPrimaryColumnName(ResultMap resultMap) {
        ResultMapping resultMapping = null;
        if (resultMap != null) {
            if (resultMap.getIdResultMappings().size() > 0)
                resultMapping = resultMap.getIdResultMappings().get(0);
        }
        if (resultMapping != null)
            return resultMapping.getColumn();
        return null;
    }

    private static ResultMapping getResultMapping(ResultMap resultMap, String fieldName) {
        if (resultMap != null) {
            for (ResultMapping resultMapping : resultMap.getResultMappings()) {
                if (resultMapping.getProperty().equals(fieldName))
                    return resultMapping;
            }
        }
        return null;
    }

    private static Method getMapperMethod(MappedStatement mappedStatement, Class<?> mapperClass, Class<?>... parameterTypes) {
        String mappedStatementId = mappedStatement.getId();
        String methodName = mappedStatementId.substring(mappedStatementId.lastIndexOf(".") + 1);
        Method[] methods = mapperClass.getMethods();
        for (Method method : methods) {
            if (!method.getName().equals(methodName))
                continue;
            Class<?>[] types = method.getParameterTypes();
            if (types.length == parameterTypes.length) {
                boolean isEqual = true;
                for (int i = 0; i < types.length; i++) {
                    if (types[i] == Object.class)
                        continue;
                    if (types[i] != parameterTypes[i])
                        isEqual = false;
                }
                if (isEqual)
                    return method;
            }
        }
        return null;
    }



    private static Field[] getModelField(Class<?> modelClass) {
        List<Field> fields = new ArrayList<>();
        Field[] declaredFields = modelClass.getDeclaredFields();
        for (Field field : declaredFields) {
            if ("serialVersionUID".equals(field.getName()))
                continue;
            fields.add(field);
        }
        return fields.toArray(new Field[0]);
    }

    private static String toUnderline(String str) {
        StringBuilder buf = new StringBuilder();
        buf.append(Character.toLowerCase(str.charAt(0)));
        for (int i = 1; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                buf.append("_" + Character.toLowerCase(c));
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
