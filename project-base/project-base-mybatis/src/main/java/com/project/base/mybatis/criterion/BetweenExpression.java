package com.project.base.mybatis.criterion;

import java.text.MessageFormat;
import java.util.List;

public class BetweenExpression implements Criterion{
    private static final long serialVersionUID = -1252205471931834227L;
    private String propertyName;
    private Object low;
    private Object high;
    private static final String sqlFormat  = " {0} between {1} and {2} ";

    protected BetweenExpression(String propertyName, Object low, Object high) {
        this.propertyName = propertyName;
        this.low = low;
        this.high = high;
    }

    @Override
    public String toSqlString(String ognlPrefix, List<String> parameterCollection) {
        parameterCollection.add("#{" + ognlPrefix + ".low}");
        parameterCollection.add("#{" + ognlPrefix + ".high}");
        return  MessageFormat.format(sqlFormat, propertyName, "#{" + ognlPrefix + ".low}","#{" + ognlPrefix + ".high}");
    }

    public Object getLow() {
        return low;
    }

    public void setLow(Object low) {
        this.low = low;
    }

    public Object getHigh() {
        return high;
    }

    public void setHigh(Object high) {
        this.high = high;
    }
}
