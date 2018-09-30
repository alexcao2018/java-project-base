package com.project.base.model;

import java.io.Serializable;
import java.util.List;

/**
 * api 统一返回分页
 * @param <T>
 */
public class PageListVo<T> implements Serializable {
    private List<T> data;

    private Integer index;

    private Integer size;

    private Integer totalCount;

    private boolean hasNext;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
