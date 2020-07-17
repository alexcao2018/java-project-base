package com.project.base.dubbo.batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhanghc on 2017-08-23.
 */
public class BatchConfiguration {

    @Bean(name = "batchProxyBeanFactory")
    public BatchProxyBeanFactory batchProxyBeanFactory() {
        return new BatchProxyBeanFactory();
    }
}
