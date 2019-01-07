package com.project.base.dubbo.batch;

import java.util.List;

public interface IDubboBatchCallback {
    void invokeCompleted(List resultCollection,Exception ex);
}
