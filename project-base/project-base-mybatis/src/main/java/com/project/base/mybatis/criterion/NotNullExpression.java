package com.project.base.mybatis.criterion;

import java.util.List;

public class NotNullExpression implements Criterion{

    private String propertyName;

    protected NotNullExpression(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toSqlString(String ognlPrefix, List<String> parameterCollection) {
        return propertyName + " is not null";
    }
}
