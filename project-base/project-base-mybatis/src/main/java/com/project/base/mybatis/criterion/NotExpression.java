package com.project.base.mybatis.criterion;

import java.util.List;

public class NotExpression implements Criterion {

    private Criterion criterion;

    protected NotExpression(Criterion criterion) {
        this.criterion = criterion;
    }

    @Override
    public String toSqlString(String ognlPrefix, List<String> parameterCollection) {
        return " not " + criterion.toSqlString(ognlPrefix + ".criterion", parameterCollection);
    }
}
