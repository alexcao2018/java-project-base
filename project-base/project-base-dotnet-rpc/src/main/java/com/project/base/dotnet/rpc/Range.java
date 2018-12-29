package com.project.base.dotnet.rpc;

import java.io.Serializable;

public class Range<T> implements Serializable {

    private T left;

    /**
     * 大于等于
     */
    private boolean leftExclusive;

    private T right;

    /**
     * 小于等于
     */
    private boolean rightExclusive;

    public T getLeft() {
        return left;
    }

    public void setLeft(T left) {
        this.left = left;
    }

    public boolean isLeftExclusive() {
        return leftExclusive;
    }

    public void setLeftExclusive(boolean leftExclusive) {
        this.leftExclusive = leftExclusive;
    }

    public T getRight() {
        return right;
    }

    public void setRight(T right) {
        this.right = right;
    }

    public boolean isRightExclusive() {
        return rightExclusive;
    }

    public void setRightExclusive(boolean rightExclusive) {
        this.rightExclusive = rightExclusive;
    }
}
