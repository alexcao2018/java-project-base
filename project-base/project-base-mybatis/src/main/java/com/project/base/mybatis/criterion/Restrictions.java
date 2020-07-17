package com.project.base.mybatis.criterion;

import java.util.Collection;

public class Restrictions {

    /**
     * 等于
     *
     * @param propertyName
     * @param value
     * @return
     */
    public static SimpleExpression eq(String propertyName, Object value) {
        return eq(propertyName, value, false);
    }

    /**
     * 等于
     *
     * @param propertyName
     * @param value
     * @param ignoreIfValueNull
     * @return
     */
    public static SimpleExpression eq(String propertyName, Object value, boolean ignoreIfValueNull) {
        if (ignoreIfValueNull && value == null)
            return null;
        return new SimpleExpression(propertyName, value, "=");
    }


    /**
     * 不等于
     *
     * @param propertyName
     * @param value
     * @return
     */
    public static SimpleExpression ne(String propertyName, Object value) {
        return ne(propertyName, value, false);
    }

    /**
     * 不等于
     *
     * @param propertyName
     * @param value
     * @param ignoreIfValueNull
     * @return
     */
    public static SimpleExpression ne(String propertyName, Object value, boolean ignoreIfValueNull) {
        if (ignoreIfValueNull && value == null)
            return null;
        return new SimpleExpression(propertyName, value, "<>");
    }

    /**
     * 大于
     *
     * @param propertyName
     * @param value
     * @return
     */
    public static SimpleExpression gt(String propertyName, Object value) {
        return gt(propertyName, value, false);
    }

    /**
     * 大于
     *
     * @param propertyName
     * @param value
     * @param ignoreIfValueNull
     * @return
     */
    public static SimpleExpression gt(String propertyName, Object value, boolean ignoreIfValueNull) {
        if (ignoreIfValueNull && value == null)
            return null;
        return new SimpleExpression(propertyName, value, ">");
    }

    /**
     * 小于
     *
     * @param propertyName
     * @param value
     * @return
     */
    public static SimpleExpression lt(String propertyName, Object value) {
        return lt(propertyName, value, false);
    }

    /**
     * 小于
     *
     * @param propertyName
     * @param value
     * @param ignoreIfValueNull
     * @return
     */
    public static SimpleExpression lt(String propertyName, Object value, boolean ignoreIfValueNull) {
        if (ignoreIfValueNull && value == null)
            return null;
        return new SimpleExpression(propertyName, value, "<");
    }


    /**
     * 小于等于
     *
     * @param propertyName
     * @param value
     * @return
     */
    public static SimpleExpression le(String propertyName, Object value) {
        return le(propertyName, value, false);
    }

    /**
     * 小于等于
     *
     * @param propertyName
     * @param value
     * @param ignoreIfValueNull
     * @return
     */
    public static SimpleExpression le(String propertyName, Object value, boolean ignoreIfValueNull) {
        if (ignoreIfValueNull && value == null)
            return null;
        return new SimpleExpression(propertyName, value, "<=");
    }

    /**
     * 在之间
     *
     * @param propertyName
     * @param low
     * @param high
     * @return
     */
    public static Criterion between(String propertyName, Object low, Object high) {
        return between(propertyName, low, high, false);
    }

    /**
     * 在之间
     *
     * @param propertyName
     * @param low
     * @param high
     * @param ignoreIfValueNull
     * @return
     */
    public static Criterion between(String propertyName, Object low, Object high, boolean ignoreIfValueNull) {
        if (ignoreIfValueNull && (high == null || low == null))
            return null;
        return new BetweenExpression(propertyName, low, high);
    }

    /**
     * 大于等于
     *
     * @param propertyName
     * @param value
     * @return
     */
    public static SimpleExpression ge(String propertyName, Object value) {
        return ge(propertyName, value, false);
    }

    /**
     * 大于等于
     *
     * @param propertyName
     * @param value
     * @param ignoreIfValueNull
     * @return
     */
    public static SimpleExpression ge(String propertyName, Object value, boolean ignoreIfValueNull) {
        if (ignoreIfValueNull && value == null)
            return null;
        return new SimpleExpression(propertyName, value, ">=");
    }

    /**
     * in
     *
     * @param propertyName
     * @param values
     * @return
     */
    public static Criterion in(String propertyName, Object... values) {
        return new InExpression(propertyName, values);
    }

    /**
     * in
     *
     * @param propertyName
     * @param values
     * @return
     */
    public static Criterion in(String propertyName, Collection values) {
        return new InExpression(propertyName, values.toArray());
    }

    /**
     * not in
     *
     * @param propertyName
     * @param values
     * @return
     */
    public static Criterion notIn(String propertyName, Object... values) {
        return new NotInExpression(propertyName, values);
    }


    /**
     * is null
     *
     * @param propertyName
     * @return
     */
    public static Criterion isNull(String propertyName) {
        return new NullExpression(propertyName);
    }

    /**
     * is not null
     *
     * @param propertyName
     * @return
     */
    public static Criterion isNotNull(String propertyName) {
        return new NotNullExpression(propertyName);
    }

    /**
     * not
     *
     * @param expression
     * @return
     */
    public static Criterion not(Criterion expression) {
        return new NotExpression(expression);
    }

    /**
     * like
     *
     * @param propertyName
     * @param value
     * @return
     */
    public static LikeExpression like(String propertyName, String value) {
        return like(propertyName, value, MatchMode.ANYWHERE, false);
    }

    /**
     * like
     *
     * @param propertyName
     * @param value
     * @param ignoreIfValueNull
     * @return
     */
    public static LikeExpression like(String propertyName, String value, boolean ignoreIfValueNull) {
        return like(propertyName, value, MatchMode.ANYWHERE, ignoreIfValueNull);
    }

    /**
     * like
     *
     * @param propertyName
     * @param value
     * @param matchMode
     * @param ignoreIfValueNull
     * @return
     */
    public static LikeExpression like(String propertyName, String value, MatchMode matchMode, boolean ignoreIfValueNull) {
        if (ignoreIfValueNull && value == null)
            return null;
        return new LikeExpression(propertyName, value, matchMode, true);
    }

    /**
     * 正则匹配
     * @param propertyName
     * @param value
     * @return
     */
    public static SimpleExpression regexp(String propertyName, Object value) {
        return regexp(propertyName, value, false);
    }

    /**
     * 正则匹配
     * @param propertyName
     * @param value
     * @param ignoreIfValueNull
     * @return
     */
    public static SimpleExpression regexp(String propertyName, Object value, boolean ignoreIfValueNull) {
        if (ignoreIfValueNull && value == null)
            return null;
        return new SimpleExpression(propertyName, value, "REGEXP");
    }


    /**
     * equal property
     *
     * @param propertyName
     * @param otherPropertyName
     * @return
     */
    public static PropertyExpression eqProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, "=");
    }

    /**
     * not equal property
     *
     * @param propertyName
     * @param otherPropertyName
     * @return
     */
    public static PropertyExpression neProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, "<>");
    }

    /**
     * less than property
     *
     * @param propertyName
     * @param otherPropertyName
     * @return
     */
    public static PropertyExpression ltProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, "<");
    }

    /**
     * less and equal property
     *
     * @param propertyName
     * @param otherPropertyName
     * @return
     */
    public static PropertyExpression leProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, "<=");
    }

    /**
     * great than property
     *
     * @param propertyName
     * @param otherPropertyName
     * @return
     */
    public static PropertyExpression gtProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, ">");
    }

    /**
     * great and equal property
     *
     * @param propertyName
     * @param otherPropertyName
     * @return
     */
    public static PropertyExpression geProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, ">=");
    }

    public static Criterion sizeEq(String propertyName, int size) {
        return new SizeExpression(propertyName, size, "=");
    }

    public static Criterion sizeNe(String propertyName, int size) {
        return new SizeExpression(propertyName, size, "<>");
    }

    public static Criterion sizeGt(String propertyName, int size) {
        return new SizeExpression(propertyName, size, "<");
    }

    public static Criterion sizeLt(String propertyName, int size) {
        return new SizeExpression(propertyName, size, ">");
    }

    public static Criterion sizeGe(String propertyName, int size) {
        return new SizeExpression(propertyName, size, "<=");
    }

    public static Criterion sizeLe(String propertyName, int size) {
        return new SizeExpression(propertyName, size, ">=");
    }

    public static Criterion and(Criterion... conditions) {
        return new Conjunction(conditions);
    }

    public static Criterion or(Criterion... conditions) {
        return new Disjunction(conditions);
    }

    public static Criterion conjunction(Criterion... conditions) {
        return new Conjunction(conditions);
    }

    public static Criterion disjunction(Criterion... conditions) {
        return new Disjunction(conditions);
    }

}
