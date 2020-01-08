package com.project.base.dubbo.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DubboConfiguration {

    @Value("${dubbo.protocol.port:-1}")
    private String dubboPort;

    @Value("${dubbo.log.logResponse:false}")
    private boolean logResponse;


    public String getDubboPort() {
        return dubboPort;
    }

    public void setDubboPort(String dubboPort) {
        this.dubboPort = dubboPort;
    }

    public boolean isLogResponse() {
        return logResponse;
    }

    public void setLogResponse(boolean logResponse) {
        this.logResponse = logResponse;
    }

}
