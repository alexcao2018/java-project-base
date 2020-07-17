package com.project.base.mybatis.criterion;

import java.text.MessageFormat;
import java.util.List;

public class InExpression implements Criterion {
    private static final long serialVersionUID = 8994481501197032010L;
    private String propertyName;
    private Object[] values;
    private static final String sqlFormat = "{0} in ({1})";

    protected InExpression(String propertyName, Object[] values) {
        this.propertyName = propertyName;
        this.values = values;
    }

    @Override
    public String toSqlString(String ognlPrefix, List<String> parameterCollection) {
        if (values == null || values.length == 0)
            return "1<>1";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i != values.length - 1)
                sb.append("#{" + ognlPrefix + ".values[" + i + "]},");
            else
                sb.append("#{" + ognlPrefix + ".values[" + i + "]}");

            parameterCollection.add("#{" + ognlPrefix + ".values[" + i + "]}");
        }
        return MessageFormat.format(sqlFormat, propertyName, sb.toString());
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }


}
