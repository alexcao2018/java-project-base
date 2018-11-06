package com.project.base.web.filter;

import com.project.base.web.GlobalExceptionHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@Component
@WebFilter(urlPatterns = "/*")
public class RequestWrapperFilter extends OncePerRequestFilter {

    public static final String _KEY_CONTENT_CACHING_REQUEST_WRAPPER = "_KEY_CONTENT_CACHING_REQUEST_WRAPPER";
    private static Logger logger = LoggerFactory.getLogger(RequestWrapperFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if ("/".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        StopWatch stopWatch = StopWatch.createStarted();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        request.setAttribute(_KEY_CONTENT_CACHING_REQUEST_WRAPPER, requestWrapper);
        filterChain.doFilter(requestWrapper, response);


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
        String httpRequestLog = MessageFormat.format("请求url:{1},执行时间:{0},请求体:{2}", milliSeconds, httpRequestUrl, StringUtils.isBlank(httpPostBody) ? StringUtils.EMPTY : httpPostBody);
        logger.info(httpRequestLog);
    }
}
