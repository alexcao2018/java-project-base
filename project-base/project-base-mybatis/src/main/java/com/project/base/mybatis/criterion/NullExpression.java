package com.project.base.mybatis.criterion;

import java.util.List;

public class NullExpression implements Criterion {

    private String propertyName;

    protected NullExpression(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toSqlString(String ognlPrefix, List<String> parameterCollection) {
        return propertyName + " is null";
    }
}
