package com.project.base.dubbo.batch;

import java.util.List;

public interface IDubboBatchCallback {
    /**
     *
     * @param resultCollection 分批调用结果集合
     * @param ex 分批调用过程中的异常信息
     */
    void invokeCompleted(List resultCollection,Exception ex);
}
