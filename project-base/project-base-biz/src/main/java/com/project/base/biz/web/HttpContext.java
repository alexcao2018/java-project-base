package com.project.base.biz.web;

import com.project.base.common.enums.EnumHttpRequestKey;
import com.project.base.model.net.HttpRequestInfo;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class HttpContext {
    public static HttpRequestInfo getHttpRequestInfo() {
        HttpRequestInfo httpRequestInfo = (HttpRequestInfo) RequestContextHolder.getRequestAttributes().getAttribute(EnumHttpRequestKey.RequestInfo.getName(), RequestAttributes.SCOPE_REQUEST);
        return httpRequestInfo;
    }
}
