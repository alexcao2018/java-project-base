package com.project.base.mybatis.criterion;

import java.text.MessageFormat;
import java.util.List;

public class SizeExpression implements Criterion{
    private String propertyName;
    private int size;
    private String op;
    private static final String sqlFormat  = " LENGTH({0}) {1} {2} ";

    protected SizeExpression(String propertyName, int size, String op) {
        this.propertyName = propertyName;
        this.size = size;
        this.op = op;
    }

    @Override
    public String toSqlString(String ognlPrefix, List<String> parameterCollection) {
        parameterCollection.add("#{" + ognlPrefix + ".size}");
        return MessageFormat.format(sqlFormat,propertyName,op,"#{" + ognlPrefix + ".size}");
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
