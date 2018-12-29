package com.project.base.web.filter;

import com.project.base.dotnet.rpc.UrlCityParser;
import com.project.base.dotnet.rpc.context.DotnetRpcContext;
import com.project.base.dotnet.rpc.context.DotnetRpcContextManager;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Yangchao.
 * Date: 2018/12/29
 */
@Component
@WebFilter(urlPatterns = "/*")
public class DotnetRpcContextFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String url = request.getRequestURI();
            String city = UrlCityParser.getCityValue(url);
            DotnetRpcContextManager.save(DotnetRpcContext.Builder.newBuilder().urlCityValue(city).build());
            filterChain.doFilter(request, response);
        } finally {
            DotnetRpcContextManager.clear();
        }
    }
}
