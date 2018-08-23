package com.project.base.mybatis.init;

import com.project.base.mybatis.base.BaseSqlProviderInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class ApplicationStartListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        BaseSqlProviderInitializer.init();
    }
}
