package com.project.base.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.base.model.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class GlobalExceptionResolver extends ExceptionHandlerExceptionResolver {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HandlerMethod handlerMethod, Exception exception) {
        String uri = httpServletRequest.getRequestURI();
        logger.error("异常url:" + uri, exception);

        CommonResponse response = new CommonResponse();
        if (exception.getClass() == NoHandlerFoundException.class) {
            response.setError(999);
            response.setMessage(exception.getMessage());
        } else {
            response.setError(999);
            StringBuilder message = new StringBuilder();
            message.append(exception.getMessage());
            for (StackTraceElement e : exception.getStackTrace()) {
                message.append(System.getProperty("line.separator"));
                message.append(e);
            }
            response.setMessage(message.toString());
        }

        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        try {
            String json = objectMapper.writeValueAsString(response);
            httpServletResponse.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ModelAndView();

    }
}
