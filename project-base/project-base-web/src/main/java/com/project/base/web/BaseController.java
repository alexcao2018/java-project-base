package com.project.base.web;

import com.project.base.common.enums.EnumHttpRequestKey;
import com.project.base.model.net.HttpRequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhanghc on 2017-05-08.
 */
public class BaseController {

    private static Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    public ServletContext context;
    @Autowired
    public HttpServletRequest request;
    @Autowired
    public HttpServletResponse response;

    public HttpRequestInfo getHttpRequestInfo() {
        HttpRequestInfo httpRequestInfo = (HttpRequestInfo) request.getAttribute(EnumHttpRequestKey.RequestInfo.getName());
        return httpRequestInfo;
    }

}
