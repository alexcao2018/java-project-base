package com.project.base.mybatis.criterion;

import java.text.MessageFormat;
import java.util.List;

public class PropertyExpression implements Criterion {

    private String propertyName;
    private String otherPropertyName;
    private String op;
    private static final String sqlFormat = "{0} {1} {2}";

    protected PropertyExpression(String propertyName, String otherPropertyName, String op) {
        this.propertyName = propertyName;
        this.otherPropertyName = otherPropertyName;
        this.op = op;
    }

    public String getOp() {
        return op;
    }

    @Override
    public String toSqlString(String ognlPrefix, List<String> parameterCollection) {
        return MessageFormat.format(sqlFormat, propertyName, op, otherPropertyName);
    }
}
