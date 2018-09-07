package com.project.base.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.base.web.convert.StringToDateConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Configuration
public class BaseWebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(new WebHandlerInterceptor());
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateConverter());
    }


    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        boolean isSetMappingJackson2HttpMessageConverter = false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

        for (HttpMessageConverter<?> httpMessageConverter : converters) {
            if (httpMessageConverter.getClass() != MappingJackson2HttpMessageConverter.class)
                continue;
            MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) httpMessageConverter;
            mappingJackson2HttpMessageConverter.getObjectMapper().setDateFormat(simpleDateFormat);
            isSetMappingJackson2HttpMessageConverter = true;
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
    }
}
