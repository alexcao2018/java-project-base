package com.project.base.dotnet.rpc;

import java.util.List;

public class PageData<T> {

    /**
     * 分页时返回总条数
     */
    private Integer count;

    /**
     * 结果集
     */
    private T result;

    public PageData(Integer count, T result) {
        this.count = count;
        this.result = result;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public static final <T> PageData<T> data(T result){
        return new PageData<>(null, result);
    }
    public static final <T> PageData<T> data(Integer count, List<T> result){
        return new PageData(count, result);
    }
}
