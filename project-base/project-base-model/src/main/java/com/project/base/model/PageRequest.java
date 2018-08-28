package com.project.base.model;

import java.io.Serializable;

public class PageRequest implements Serializable {
    private int pageIndex;
    private int pageSize;

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return this.pageSize;
    }
}
