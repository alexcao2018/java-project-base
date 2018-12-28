package com.project.base.web;

import com.project.base.common.enums.EnumHttpRequestKey;
import com.project.base.model.CommonResponse;
import com.project.base.web.filter.LogFilter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

/**
 * @EnableWebMvc 加此标记，覆盖springboot 默认处理异常配置
 * 如果为404 400 此类请求，未进入到action 内部，无法匹配异常
 * 需要设置dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private HttpServletRequest httpServletRequest;

    @ExceptionHandler({NoHandlerFoundException.class})
    @ResponseBody
    public CommonResponse handleNoHandlerFoundException(Throwable ex) throws IOException {
        logger.error(ex.getMessage(), ex);
        CommonResponse response = new CommonResponse();
        response.setError(999);
        response.setMessage(ex.getMessage());
        return response;
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public CommonResponse handleException(Throwable ex) throws IOException {

        String httpRequestUrl = httpServletRequest.getRequestURL().toString();
        if (StringUtils.isNotBlank(httpServletRequest.getQueryString())) {
            httpRequestUrl = httpRequestUrl + "?" + httpServletRequest.getQueryString();
        }
        httpRequestUrl = URLDecoder.decode(httpRequestUrl, StandardCharsets.UTF_8.name());

        String httpPostBody = StringUtils.EMPTY;
        if (HttpMethod.POST.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
            Object requestWrapperObject = httpServletRequest.getAttribute(EnumHttpRequestKey.RequestWrapper.getName());
            if (requestWrapperObject != null) {
                ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) requestWrapperObject;
                httpPostBody = IOUtils.toString(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8.name());
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
        return response;
    }

}