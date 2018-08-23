package com.project.base.mybatis.criterion;

import java.text.MessageFormat;
import java.util.List;

public class SimpleExpression implements Criterion {

    private String propertyName;
    private Object value;
    private String op;
    private static final String sqlFormat = "{0} {1} {2}";

    public SimpleExpression(String propertyName, Object value, String op) {
        this.propertyName = propertyName;
        this.value = value;
        this.op = op;
    }

    @Override
    public String toSqlString(String ognlPrefix, List<String> parameterCollection) {
        parameterCollection.add("#{" + ognlPrefix + ".value}");
        return MessageFormat.format(sqlFormat, propertyName, op, "#{" + ognlPrefix + ".value}");
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
