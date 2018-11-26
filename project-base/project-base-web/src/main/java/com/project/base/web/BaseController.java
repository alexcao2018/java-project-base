package com.project.base.web;

import com.project.base.web.filter.LogFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zhanghc on 2017-05-08.
 */
public class BaseController {

    private static Logger logger = LoggerFactory.getLogger(BaseController.class);
    private static HttpRequestInfo defaultHttpRequestInfo = new HttpRequestInfo();

    @Autowired
    public ServletContext context;
    @Autowired
    public HttpServletRequest request;
    @Autowired
    public HttpServletResponse response;

    public HttpRequestInfo getHttpRequestInfo() {

        HttpRequestInfo httpRequestInfo = (HttpRequestInfo) request.getAttribute(LogFilter._KEY_HTTP_REQUEST_INFO);
        if (httpRequestInfo != null)
            return httpRequestInfo;

        ContentCachingRequestWrapper contentCachingRequestWrapper = (ContentCachingRequestWrapper) request.getAttribute(LogFilter._KEY_CONTENT_CACHING_REQUEST_WRAPPER);
        if (contentCachingRequestWrapper == null) {
            request.setAttribute(LogFilter._KEY_HTTP_REQUEST_INFO, defaultHttpRequestInfo);
            return defaultHttpRequestInfo;
        }
        try {
            httpRequestInfo = LogFilter.getHttpRequestInfo(contentCachingRequestWrapper);
            request.setAttribute(LogFilter._KEY_HTTP_REQUEST_INFO, httpRequestInfo);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return httpRequestInfo;
    }

}
