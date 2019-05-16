package com.project.base.web.filter;

import com.project.base.common.enums.EnumHttpRequestKey;
import com.project.base.model.net.HttpRequestInfo;
import com.project.base.trace.TraceIdGenerator;
import com.project.base.web.annotation.LogRequest;
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
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@Component
@WebFilter(urlPatterns = "/*")
public class LogFilter extends OncePerRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(LogFilter.class);

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if ("/".equals(request.getRequestURI()) || request.getRequestURI().equals("/wechat/")
                || request.getRequestURI().equals("/healthcheck/")) {
            filterChain.doFilter(request, response);
            return;
        }

        /* 设置trace id
        ----------------------------------------------------------
         */
        TraceIdGenerator.generateTraceId();

        boolean isCacheResponse = isLogResponse(request);
        String responseContent = StringUtils.EMPTY;
        StopWatch stopWatch = StopWatch.createStarted();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = null;
        request.setAttribute(EnumHttpRequestKey.RequestWrapper.getName(), requestWrapper);
        if (isCacheResponse) {
            responseWrapper = new ContentCachingResponseWrapper(response);
            filterChain.doFilter(requestWrapper, responseWrapper);
            responseContent = getResponseInfo(responseWrapper);
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

        /* 记录请求信息
        -------------------------------------------------------------------
         */
        HttpRequestInfo httpRequestInfo = getHttpRequestInfo(requestWrapper);

        String httpRequestLog = StringUtils.EMPTY;
        if (isCacheResponse) {
            httpRequestLog = MessageFormat.format("{3},请求url:【{1}】,执行时间:{0}{2},请求响应:{4}"
                    , milliSeconds
                    , httpRequestInfo.getUrl()
                    , StringUtils.isBlank(httpRequestInfo.getBody()) ? StringUtils.EMPTY : ",请求体:" + httpRequestInfo.getBody()
                    , httpRequestInfo.getMethod()
                    , responseContent);
        } else {
            httpRequestLog = MessageFormat.format("{3},请求url:【{1}】,执行时间:{0}{2}"
                    , milliSeconds
                    , httpRequestInfo.getUrl()
                    , StringUtils.isBlank(httpRequestInfo.getBody()) ? StringUtils.EMPTY : ",请求体:" + httpRequestInfo.getBody()
                    , request.getMethod());
        }
        logger.info(httpRequestLog);
    }

    private boolean isLogResponse(HttpServletRequest request) {
        try {
            HandlerExecutionChain handler = handlerMapping.getHandler(request);
            if (handler == null)
                return false;
            LogResponse logResponseAnnotation = ((HandlerMethod) handler.getHandler()).getMethod().getAnnotation(LogResponse.class);
            return logResponseAnnotation != null && logResponseAnnotation.value();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    private boolean isLogRequest(HttpServletRequest request) {
        try {
            HandlerExecutionChain handler = handlerMapping.getHandler(request);
            if (handler == null)
                return false;
            LogRequest logRequestAnnotation = ((HandlerMethod) handler.getHandler()).getMethod().getAnnotation(LogRequest.class);
            return logRequestAnnotation != null && logRequestAnnotation.value();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return true;
    }

    public static HttpRequestInfo getHttpRequestInfo(ContentCachingRequestWrapper requestWrapper) throws IOException {
        String httpRequestUrl = requestWrapper.getRequestURL().toString();
        if (StringUtils.isNotBlank(requestWrapper.getQueryString())) {
            httpRequestUrl = httpRequestUrl + "?" + requestWrapper.getQueryString();
        }

        String httpPostBody = StringUtils.EMPTY;
        if (HttpMethod.POST.name().equalsIgnoreCase(requestWrapper.getMethod())) {
            httpPostBody = IOUtils.toString(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8.name());
        }

        HttpRequestInfo httpRequestInfo = new HttpRequestInfo();
        httpRequestInfo.setBody(httpPostBody);
        httpRequestInfo.setMethod(requestWrapper.getMethod());
        httpRequestInfo.setUrl(URLDecoder.decode(httpRequestUrl, StandardCharsets.UTF_8.name()));
        httpRequestInfo.setUri(requestWrapper.getRequestURI());

        return httpRequestInfo;
    }

    private String getResponseInfo(ContentCachingResponseWrapper responseWrapper) {
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
