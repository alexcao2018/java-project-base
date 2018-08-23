package com.project.base.mybatis.criterion;

import java.util.Collection;

public class Restrictions {
    public static SimpleExpression eq(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "=");
    }

    public static SimpleExpression ne(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "<>");
    }

    public static SimpleExpression gt(String propertyName, Object value) {
        return new SimpleExpression( propertyName, value, ">" );
    }

    public static SimpleExpression lt(String propertyName, Object value) {
        return new SimpleExpression( propertyName, value, "<" );
    }

    public static SimpleExpression le(String propertyName, Object value) {
        return new SimpleExpression( propertyName, value, "<=" );
    }

    public static Criterion between(String propertyName, Object low, Object high) {
        return new BetweenExpression( propertyName, low, high );
    }

    public static SimpleExpression ge(String propertyName, Object value) {
        return new SimpleExpression( propertyName, value, ">=" );
    }

    public static Criterion in(String propertyName, Object... values) {
        return new InExpression(propertyName, values);
    }

    public static Criterion in(String propertyName, Collection values) {
        return new InExpression(propertyName, values.toArray());
    }

    public static Criterion isNull(String propertyName) {
        return new NullExpression(propertyName);
    }

    public static Criterion isNotNull(String propertyName) {
        return new NotNullExpression(propertyName);
    }

    public static Criterion not(Criterion expression) {
        return new NotExpression(expression);
    }

    public static LikeExpression like(String propertyName, String value) {
        return new LikeExpression(propertyName, value, MatchMode.ANYWHERE, true);
    }

    public static LikeExpression like(String propertyName, String value, MatchMode matchMode) {
        return new LikeExpression(propertyName, value, matchMode, true);
    }

    public static PropertyExpression eqProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression( propertyName, otherPropertyName, "=" );
    }

    public static PropertyExpression neProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression( propertyName, otherPropertyName, "<>" );
    }

    public static PropertyExpression ltProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression( propertyName, otherPropertyName, "<" );
    }

    public static PropertyExpression leProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression( propertyName, otherPropertyName, "<=" );
    }

    public static PropertyExpression gtProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression( propertyName, otherPropertyName, ">" );
    }

    public static PropertyExpression geProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression( propertyName, otherPropertyName, ">=" );
    }

    public static Criterion sizeEq(String propertyName, int size) {
        return new SizeExpression( propertyName, size, "=" );
    }

    public static Criterion sizeNe(String propertyName, int size) {
        return new SizeExpression( propertyName, size, "<>" );
    }

    public static Criterion sizeGt(String propertyName, int size) {
        return new SizeExpression( propertyName, size, "<" );
    }

    public static Criterion sizeLt(String propertyName, int size) {
        return new SizeExpression( propertyName, size, ">" );
    }

    public static Criterion sizeGe(String propertyName, int size) {
        return new SizeExpression( propertyName, size, "<=" );
    }

    public static Criterion sizeLe(String propertyName, int size) {
        return new SizeExpression( propertyName, size, ">=" );
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
