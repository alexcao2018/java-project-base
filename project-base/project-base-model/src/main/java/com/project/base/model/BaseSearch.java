package com.project.base.model;

import io.swagger.annotations.ApiModelProperty;

public class BaseSearch {

    @ApiModelProperty("必须，页码（从0开始）")
    private Integer pageIndex = 0;
    @ApiModelProperty("必须，分页条数")
    private Integer pageSize = 20;
    @ApiModelProperty("非必须，排序字段名")
    private String orderName;
    @ApiModelProperty("非必须，排序方向")
    private String orderDirection;

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }
}
