package com.project.base.web;

import com.project.base.model.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

@ControllerAdvice
@EnableWebMvc
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

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