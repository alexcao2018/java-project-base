package com.project.base.web;

import com.project.base.common.enums.EnumHttpRequestKey;
import com.project.base.model.net.HttpRequestInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

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
        HttpRequestInfo httpRequestInfo = (HttpRequestInfo) RequestContextHolder.getRequestAttributes().getAttribute(EnumHttpRequestKey.RequestInfo.getName(), RequestAttributes.SCOPE_REQUEST);
        ContentCachingRequestWrapper contentCachingRequestWrapper = (ContentCachingRequestWrapper) RequestContextHolder.getRequestAttributes().getAttribute(EnumHttpRequestKey.RequestWrapper.getName(), RequestAttributes.SCOPE_REQUEST);
        if (contentCachingRequestWrapper == null || StringUtils.isNotBlank(httpRequestInfo.getBody()))
            return httpRequestInfo;
        try {
            if (HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
                httpRequestInfo.setBody(IOUtils.toString(contentCachingRequestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8.name()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return httpRequestInfo;
    }

}
