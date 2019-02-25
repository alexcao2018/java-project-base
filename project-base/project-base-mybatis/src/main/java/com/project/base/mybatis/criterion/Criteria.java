package com.project.base.mybatis.criterion;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

public class Criteria implements Serializable {

    private static final long serialVersionUID = -2654218265471632634L;
    private Class entityClazz;
    private Integer firstResult;
    private Integer maxResult;
    private Long recordCount;
    private List<Order> orderEntries = new ArrayList<>();
    private List<Criterion> criterionEntries = new ArrayList<>();
    private List<String> parameterCollection = new ArrayList<>();
    private List<String> projectionCollection = null;
    private boolean distinctFlag = false;

    private Criteria(Class entityClazz) {
        this.entityClazz = entityClazz;
    }

    public static Criteria forClass(Class clazz) {
        return new Criteria(clazz);
    }

    public Criteria add(Criterion expression) {
        if(expression==null)
            return this;
        criterionEntries.add(expression);
        return this;
    }

    public Criteria addOrder(Order order) {
        orderEntries.add(order);
        return this;
    }

    public Criteria addOrder(Order... orderCollection) {
        orderEntries.addAll(Arrays.asList(orderCollection));
        return this;
    }

    public Criteria addOrder(String orderProperty, String orderDirection) {
        if (StringUtils.isEmpty(orderProperty) || StringUtils.isEmpty(orderDirection)) {
            return this;
        }
        String[] orderProperties = orderProperty.split("#");
        String[] orderDirections = orderDirection.split("#");
        int length = orderProperties.length;
        int direcLength = orderDirections.length;
        for(int i = 0; i < length; i++) {
            String property = orderProperties[i];
            if (i > direcLength - 1) {
                break;
            }
            String direction = orderDirections[i];
            orderEntries.add(new Order(property, direction.equals("1")));
        }
        return this;
    }

    public Criteria setProjection(String... projectionCollection) {
        this.projectionCollection = Arrays.asList(projectionCollection);
        return this;
    }

    public Criteria distinct() {
        distinctFlag = true;
        return this;
    }

    public String getProjectionSqlString() {
        StringBuilder sb = new StringBuilder();
        if (this.projectionCollection != null && this.projectionCollection.size() > 0) {
            if (distinctFlag) {
                sb.append(" distinct ");
            }
            for (String projection : projectionCollection) {
                sb.append(projection + ",");
            }
            return StringUtils.chop(sb.toString());
        }

        return distinctFlag ? " distinct * " : " * ";
    }

    public String toSqlString() {
        StringBuilder sql = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();
        for (int i = 0; i < criterionEntries.size(); i++) {
            Criterion criterionEntry = criterionEntries.get(i);
            whereSql.append("(");
            whereSql.append(criterionEntry.toSqlString("criteria.criterionEntries[" + i + "]", parameterCollection));
            if (i != criterionEntries.size() - 1)
                whereSql.append(") and");
            else
                whereSql.append(")");
        }

        if (StringUtils.isNotBlank(whereSql.toString())) {
            sql.append(" where ");
            sql.append(whereSql);
        }

        if (orderEntries.size() > 0) {
            sql.append(" order by ");
            for (int i = 0; i < orderEntries.size(); i++) {
                Order order = orderEntries.get(i);
                if (i != orderEntries.size() - 1) {
                    sql.append(order.toString() + ",");
                } else {
                    sql.append(order.toString());
                }
            }
        }

        if (maxResult != null && firstResult != null) {
            sql.append(MessageFormat.format(" limit {0},{1} ", firstResult, maxResult));
        } else if (maxResult != null && firstResult == null) {
            sql.append(MessageFormat.format(" limit {0} ", maxResult));
        }

        return sql.toString();
    }

    public String replaceParameter2PlaceHolder() {
        String toSql = toSqlString();
        for (String parameter : parameterCollection) {
            // TODO replace {} , 需要使用replaceAll 方法，替换所有 ？ 需要思考下
            toSql = toSql.replace(parameter, "?");
        }
        return toSql;
    }

    public Integer getFirstResult() {
        return firstResult;
    }

    public Criteria setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    public Integer getMaxResult() {
        return maxResult;
    }

    public Criteria setMaxResult(Integer maxResult) {
        this.maxResult = maxResult;
        return this;
    }

    public Class getEntityClazz() {
        return entityClazz;
    }

    public List<String> getParameterCollection() {
        return parameterCollection;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }

    public List<String> getProjectionCollection() {
        return projectionCollection;
    }

    public boolean isDistinctFlag() {
        return distinctFlag;
    }
}
