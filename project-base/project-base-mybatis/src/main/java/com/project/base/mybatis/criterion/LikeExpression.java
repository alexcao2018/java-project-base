package com.project.base.mybatis.criterion;

import java.text.MessageFormat;
import java.util.List;

public class LikeExpression implements Criterion {

    private String propertyName;
    private Object value;
    private boolean ignoreCase;
    private static String sqlFormat = "{0} {1} {2}";

    protected LikeExpression(
            String propertyName,
            String value,
            boolean ignoreCase) {
        this.propertyName = propertyName;
        this.value = value;
        this.ignoreCase = ignoreCase;
    }

    protected LikeExpression(String propertyName, String value) {
        this(propertyName, value, false);
    }

    protected LikeExpression(String propertyName, String value, MatchMode matchMode) {
        this(propertyName, matchMode.toMatchString(value));
    }

    protected LikeExpression(
            String propertyName,
            String value,
            MatchMode matchMode,
            boolean ignoreCase) {
        this(propertyName, matchMode.toMatchString(value), ignoreCase);
    }


    @Override
    public String toSqlString(String ognlPrefix, List<String> parameterCollection) {
        parameterCollection.add("#{" + ognlPrefix + ".value}");
        return MessageFormat.format(sqlFormat, propertyName, " like ", "#{" + ognlPrefix + ".value}");
    }
}
