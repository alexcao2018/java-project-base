package com.project.base.model;

import java.io.Serializable;

public class PageInfo implements Serializable {
    private static final long serialVersionUID = -4952544225277613196L;
    private int pageSize;
    private int pageNum;
    private long totalCount;

    public PageInfo(int pageIndex, int pageSize) {
        this.pageNum = pageIndex;
        this.pageSize = pageSize;
    }

    public PageInfo(int pageIndex) {
        this.pageNum = pageIndex;
        this.pageSize = 5;
    }

    public PageInfo(){
        this.pageSize = 20;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPages() {
        return (int) Math.ceil(((double) totalCount / (double) pageSize));
    }

    public boolean hasNext() {
        return this.pageNum < getTotalPages() - 1;
    }

    public boolean hasPrevious() {
        return this.pageNum > 0;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "pageSize=" + pageSize +
                ", pageNum=" + pageNum +
                ", totalCount=" + totalCount +
                '}';
    }
}
