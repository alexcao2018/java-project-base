package com.project.base.web.interceptor;

import com.project.base.common.enums.EnumHttpRequestKey;
import com.project.base.model.net.HttpRequestInfo;
import com.project.base.web.BaseController;
import com.project.base.web.filter.LogFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogInterceptor implements HandlerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        initHttpRequestInfo(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private void initHttpRequestInfo(HttpServletRequest request) {
        HttpRequestInfo httpRequestInfo = null;
        ContentCachingRequestWrapper contentCachingRequestWrapper = (ContentCachingRequestWrapper) request.getAttribute(EnumHttpRequestKey.RequestWrapper.getName());
        if (contentCachingRequestWrapper != null) {
            try {
                httpRequestInfo = LogFilter.getHttpRequestInfo(contentCachingRequestWrapper);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            httpRequestInfo = new HttpRequestInfo();
            String httpRequestUrl = request.getRequestURL().toString();
            if (StringUtils.isNotBlank(request.getQueryString())) {
                httpRequestUrl = httpRequestUrl + "?" + request.getQueryString();
            }
            httpRequestInfo.setUri(request.getRequestURI());
            httpRequestInfo.setUrl(httpRequestUrl);
            httpRequestInfo.setMethod(request.getMethod());
        }

        request.setAttribute(EnumHttpRequestKey.RequestInfo.getName(), httpRequestInfo);
    }


}
