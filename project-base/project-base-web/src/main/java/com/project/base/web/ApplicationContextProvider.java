package com.project.base.web;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> cls) {
        return applicationContext.getBean(cls);
    }

//    private void init(){
//        DispatcherServlet dispatcherServlet = getBean(DispatcherServlet.class);
//        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
//    }


}
