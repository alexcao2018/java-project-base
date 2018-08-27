package com.project.base.dubbo.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DubboConfiguration {

    @Value("${dubbo.protocol.port}")
    private String dubboPort;

    public String getDubboPort() {
        return dubboPort;
    }

    public void setDubboPort(String dubboPort) {
        this.dubboPort = dubboPort;
    }

}
