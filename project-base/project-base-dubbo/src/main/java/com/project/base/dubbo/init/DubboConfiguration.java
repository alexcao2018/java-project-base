package com.project.base.dubbo.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DubboConfiguration {

    @Value("${dubbo.protocol.port:-1}")
    private String dubboPort;

    @Value("${dubbo.log.record-invoke-result:false}")
    private boolean recordInvokeResult;

    @Value("${dubbo.log.record-invoke-exclude-method:#{null}}")
    private List<String> recordInvokeExcludeMethod;

    public String getDubboPort() {
        return dubboPort;
    }

    public void setDubboPort(String dubboPort) {
        this.dubboPort = dubboPort;
    }

    public boolean isRecordInvokeResult() {
        return recordInvokeResult;
    }

    public void setRecordInvokeResult(boolean recordInvokeResult) {
        this.recordInvokeResult = recordInvokeResult;
    }

    public List<String> getRecordInvokeExcludeMethod() {
        return recordInvokeExcludeMethod;
    }

    public void setRecordInvokeExcludeMethod(List<String> recordInvokeExcludeMethod) {
        this.recordInvokeExcludeMethod = recordInvokeExcludeMethod;
    }

}
