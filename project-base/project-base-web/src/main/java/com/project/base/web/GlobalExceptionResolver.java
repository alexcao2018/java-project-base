package com.project.base.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.base.common.enums.EnumHttpRequestKey;
import com.project.base.model.CommonResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;


public class GlobalExceptionResolver extends ExceptionHandlerExceptionResolver {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private static ModelAndView modelAndView =  new ModelAndView();

    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HandlerMethod handlerMethod, Exception ex) {

        String httpRequestUrl = httpServletRequest.getRequestURL().toString();
        if (StringUtils.isNotBlank(httpServletRequest.getQueryString())) {
            httpRequestUrl = httpRequestUrl + "?" + httpServletRequest.getQueryString();
        }
        try {
            httpRequestUrl = URLDecoder.decode(httpRequestUrl, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String httpPostBody = StringUtils.EMPTY;
        if (HttpMethod.POST.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
            Object requestWrapperObject = httpServletRequest.getAttribute(EnumHttpRequestKey.RequestWrapper.getName());
            if (requestWrapperObject != null) {
                ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) requestWrapperObject;
                try {
                    httpPostBody = IOUtils.toString(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8.name());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String httpRequestLog = MessageFormat.format("{0},请求url:{1},请求体:{2}",
                httpServletRequest.getMethod()
                , httpRequestUrl
                , StringUtils.isBlank(httpPostBody) ? StringUtils.EMPTY : httpPostBody);

        logger.error(httpRequestLog + ",异常信息：" + ex.getMessage(), ex);
        CommonResponse response = new CommonResponse();
        response.setError(999);
        StringBuilder message = new StringBuilder();
        message.append(ex.getMessage());
        for (StackTraceElement e : ex.getStackTrace()) {
            message.append(System.getProperty("line.separator"));
            message.append(e);
        }
        response.setMessage("接口异常");
        response.setException(message.toString());

        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        try {
            String json = objectMapper.writeValueAsString(response);
            httpServletResponse.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return modelAndView;

    }
}
