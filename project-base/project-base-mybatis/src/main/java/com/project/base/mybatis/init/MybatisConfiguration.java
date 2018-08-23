package com.project.base.mybatis.init;

import com.project.base.mybatis.plugin.CriteriaInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfiguration {
    @Bean
    CriteriaInterceptor criteriaInterceptor() {
        return new CriteriaInterceptor();
    }
}
