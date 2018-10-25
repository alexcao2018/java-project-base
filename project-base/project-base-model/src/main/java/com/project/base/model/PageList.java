package com.project.base.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class PageList<T> implements Serializable {

    @JsonProperty("SourceData")
    private List<T> sourceData;
    @JsonProperty("PageIndex")
    private Integer pageIndex;
    @JsonProperty("PageSize")
    private Integer pageSize;
    @JsonProperty("TotalCount")
    private Long totalCount;
    @JsonProperty("TotalPages")
    private Integer totalPages;
    @JsonProperty("HasPreviousPage")
    private Boolean hasPreviousPage;
    @JsonProperty("HasNextPage")
    private Boolean hasNextPage;

    public List<T> getSourceData() {
        return sourceData;
    }

    public void setSourceData(List<T> sourceData) {
        this.sourceData = sourceData;
    }

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

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Boolean getHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(Boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public Boolean getHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(Boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public static <T> PageList success(PageInfo pageInfo, List<T> object){
        PageList pagedList = new PageList();
        pagedList.setSourceData(object);
        pagedList.setPageIndex(pageInfo.getPageNum());
        pagedList.setPageSize(pageInfo.getPageSize());
        pagedList.setTotalCount(pageInfo.getTotalCount());
        pagedList.setTotalPages(pageInfo.getTotalPages());
        pagedList.setHasNextPage(pageInfo.hasNext());
        pagedList.setHasPreviousPage(pageInfo.hasPrevious());
        return pagedList;
    }
}
