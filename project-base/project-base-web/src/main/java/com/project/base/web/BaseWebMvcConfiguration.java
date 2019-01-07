package com.project.base.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.base.web.convert.JsonParameterResolver;
import com.project.base.web.convert.StringToDateConverter;
import com.project.base.web.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class BaseWebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(new LogInterceptor());
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        JsonParameterResolver jsonParameterResolver = new JsonParameterResolver();
        argumentResolvers.add(0, jsonParameterResolver);
        super.addArgumentResolvers(argumentResolvers);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateConverter());
    }

    /**
     * 增加全局异常处理，有一下方式
     * 1、继承 WebMvcConfigurationSupport , 覆盖 extendHandlerExceptionResolvers,并 把 GlobalExceptionResolver 插入到第一位
     * 2、覆盖 configureHandlerExceptionResolvers 方法
     *
     * @param exceptionResolvers
     */
    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        //exceptionResolvers.clear();
        exceptionResolvers.add(0, new GlobalExceptionResolver());
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        boolean isSetMappingJackson2HttpMessageConverter = false;
        boolean isSetMappingStringHttpMessageConverter = false;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

        for (HttpMessageConverter<?> httpMessageConverter : converters) {
            if (httpMessageConverter.getClass() == MappingJackson2HttpMessageConverter.class) {
                MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) httpMessageConverter;
                mappingJackson2HttpMessageConverter.getObjectMapper().setDateFormat(simpleDateFormat);
                isSetMappingJackson2HttpMessageConverter = true;
            }

            if (httpMessageConverter.getClass() == StringHttpMessageConverter.class) {
                StringHttpMessageConverter stringHttpMessageConverter = (StringHttpMessageConverter) httpMessageConverter;
                stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
                isSetMappingStringHttpMessageConverter = true;
            }

        }

        if (!isSetMappingJackson2HttpMessageConverter) {
            MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(simpleDateFormat);
            mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
            List<MediaType> list = new ArrayList<>();
            list.add(MediaType.APPLICATION_JSON_UTF8);
            mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
            converters.add(mappingJackson2HttpMessageConverter);
        }

        if (!isSetMappingStringHttpMessageConverter) {
            StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
            converters.add(stringHttpMessageConverter);
        }

    }
}
