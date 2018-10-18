package com.project.base.web.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/*")
public class RequestWrapperFilter extends OncePerRequestFilter {

    public static final String  _KEY_CONTENT_CACHING_REQUEST_WRAPPER = "_KEY_CONTENT_CACHING_REQUEST_WRAPPER";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        request.setAttribute(_KEY_CONTENT_CACHING_REQUEST_WRAPPER,requestWrapper);
        filterChain.doFilter(requestWrapper, response);

    }
}
