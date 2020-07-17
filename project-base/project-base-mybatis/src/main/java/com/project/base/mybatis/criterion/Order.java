package com.project.base.mybatis.criterion;

import java.io.Serializable;

public class Order implements Serializable {
    private boolean ascending;
    private boolean ignoreCase;
    private String propertyName;

    /**
     * Ascending order
     *
     * @param propertyName The property to order on
     * @return The build Order instance
     */
    public static Order asc(String propertyName) {
        return new Order(propertyName, true);
    }

    /**
     * Descending order.
     *
     * @param propertyName The property to order on
     * @return The build Order instance
     */
    public static Order desc(String propertyName) {
        return new Order(propertyName, false);
    }

    /**
     * Constructor for Order.  Order instances are generally created by factory methods.
     *
     * @see #asc
     * @see #desc
     */
    protected Order(String propertyName, boolean ascending) {
        this.propertyName = propertyName;
        this.ascending = ascending;
    }

    /**
     * Should this ordering ignore case?  Has no effect on non-character properties.
     *
     * @return {@code this}, for method chaining
     */
    public Order ignoreCase() {
        ignoreCase = true;
        return this;
    }


    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String toString() {
        return propertyName + ' ' + ( ascending ? "asc" : "desc" );
    }

}
