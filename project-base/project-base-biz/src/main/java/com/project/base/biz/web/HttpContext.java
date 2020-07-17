package com.project.base.biz.web;

import com.project.base.common.enums.EnumHttpRequestKey;
import com.project.base.model.net.HttpRequestInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpContext {
    public static HttpRequestInfo getHttpRequestInfo() {
        HttpRequestInfo httpRequestInfo = (HttpRequestInfo) RequestContextHolder.getRequestAttributes().getAttribute(EnumHttpRequestKey.RequestInfo.getName(), RequestAttributes.SCOPE_REQUEST);
        ContentCachingRequestWrapper contentCachingRequestWrapper = (ContentCachingRequestWrapper) RequestContextHolder.getRequestAttributes().getAttribute(EnumHttpRequestKey.RequestWrapper.getName(), RequestAttributes.SCOPE_REQUEST);
        if (contentCachingRequestWrapper == null || StringUtils.isNotBlank(httpRequestInfo.getBody()))
            return httpRequestInfo;

        try {
            if (HttpMethod.POST.name().equalsIgnoreCase(httpRequestInfo.getMethod())) {
                httpRequestInfo.setBody(IOUtils.toString(contentCachingRequestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8.name()));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return httpRequestInfo;
    }
}
