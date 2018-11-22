package com.project.base.web;

import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhanghc on 2017-05-08.
 */
public class BaseController {

    @Autowired
    public ServletContext context;
    @Autowired
    public HttpServletRequest request;
    @Autowired
    public HttpServletResponse response;
}
