package com.project.base.web.filter;

import com.project.base.web.annotation.LogResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@WebFilter(urlPatterns = "/*")
public class LogFilter extends OncePerRequestFilter {

    public static final String _KEY_CONTENT_CACHING_REQUEST_WRAPPER = "_KEY_CONTENT_CACHING_REQUEST_WRAPPER";
    public static final String _KEY_CONTENT_CACHING_RESPONSE_WRAPPER = "_KEY_CONTENT_CACHING_RESPONSE_WRAPPER";
    private static Logger logger = LoggerFactory.getLogger(LogFilter.class);

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if ("/".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isCacheResponse = isLogResponse(request);
        String responseContent = StringUtils.EMPTY;
        StopWatch stopWatch = StopWatch.createStarted();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = null;
        request.setAttribute(_KEY_CONTENT_CACHING_REQUEST_WRAPPER, requestWrapper);
        if (isCacheResponse) {
            responseWrapper = new ContentCachingResponseWrapper(response);
            filterChain.doFilter(requestWrapper, responseWrapper);
            responseContent = getResponseContent(responseWrapper);
             /* 将response内容 copy到原来的流中
            -------------------------------------------------------------------
             */
            responseWrapper.copyBodyToResponse();
        } else {
            filterChain.doFilter(requestWrapper, response);
        }


        /* 记录执行时长
        -------------------------------------------------------------------
         */
        stopWatch.stop();

        long milliSeconds = stopWatch.getTime(TimeUnit.MILLISECONDS);

        String httpRequestUrl = request.getRequestURL().toString();
        if (StringUtils.isNotBlank(request.getQueryString())) {
            httpRequestUrl = httpRequestUrl + "?" + request.getQueryString();
        }

        String httpPostBody = StringUtils.EMPTY;
        if (HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
            httpPostBody = IOUtils.toString(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8.name());
        }
        String httpRequestLog = StringUtils.EMPTY;
        if (isCacheResponse) {
            httpRequestLog = MessageFormat.format("{3},请求url:{1},执行时间:{0},请求体:{2},请求响应:{4}"
                    , milliSeconds
                    , httpRequestUrl
                    , StringUtils.isBlank(httpPostBody) ? StringUtils.EMPTY : httpPostBody, request.getMethod()
                    , responseContent);
        } else {
            httpRequestLog = MessageFormat.format("{3},请求url:{1},执行时间:{0},请求体:{2}"
                    , milliSeconds
                    , httpRequestUrl
                    , StringUtils.isBlank(httpPostBody) ? StringUtils.EMPTY : httpPostBody
                    , request.getMethod());
        }
        logger.info(httpRequestLog);
    }

    private boolean isLogResponse(HttpServletRequest request) {
        try {
            HandlerExecutionChain handler = handlerMapping.getHandler(request);
            LogResponse logResponseAnnotation = ((HandlerMethod) handler.getHandler()).getMethod().getAnnotation(LogResponse.class);
            return logResponseAnnotation != null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    private String getResponseContent(ContentCachingResponseWrapper responseWrapper) {
        try {
            int statusCode = responseWrapper.getStatusCode();
            return "状态码:" + statusCode + ",响应内容:" + new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return StringUtils.EMPTY;
    }


    /*
    private void getMappingMethod() {
        List<HashMap<String, String>> urlList = new ArrayList<HashMap<String, String>>();

        Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            PatternsRequestCondition p = info.getPatternsCondition();
            for (String url : p.getPatterns()) {
                hashMap.put("url", url);
            }
            hashMap.put("className", method.getMethod().getDeclaringClass().getName()); // 类名
            hashMap.put("method", method.getMethod().getName()); // 方法名
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            String type = methodsCondition.toString();
            if (type != null && type.startsWith("[") && type.endsWith("]")) {
                type = type.substring(1, type.length() - 1);
                hashMap.put("type", type);
            }
            urlList.add(hashMap);
        }
    }*/

}
