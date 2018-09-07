package com.project.base.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.base.model.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 *  @EnableWebMvc 加此标记，覆盖springboot 默认处理异常配置
 *  如果为404 400 此类请求，未进入到action 内部，无法匹配异常
 *  需要设置dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
 */
@ControllerAdvice
@EnableWebMvc
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


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
        logger.error(ex.getMessage(), ex);
        CommonResponse response = new CommonResponse();
        response.setError(999);
        StringBuilder message = new StringBuilder();
        message.append(ex.getMessage());
        for (StackTraceElement e : ex.getStackTrace()) {
            message.append(System.getProperty("line.separator"));
            message.append(e);
        }
        response.setMessage(message.toString());
        return response;
    }

}