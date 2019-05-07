package com.project.base.dubbo.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.base.dubbo.init.ApplicationContextAware4Dubbo;
import com.project.base.dubbo.init.DubboConfiguration;
import com.project.base.trace.TraceIdGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
/*

    第1步：
       在biz项目中引用：
       <dependency>
            <groupId>com.project.base</groupId>
            <artifactId>project-base-dubbo</artifactId>
        </dependency>

        ps: 在web-host 项目中引用不起作用。

    第2步：
       1. 在/resources/META-INF/dubbo 中建立文件
         com.alibaba.dubbo.rpc.Filter
       2. 文件中填写
         consumeLogFilter=com.project.base.dubbo.filter.ConsumeLogFilter


    第3步：

        需要在yml 文件中配置
        dubbo:
          provider:
            loadbalance: leastactive
            retries: 0
            timeout: 1200000
            filter: dubboFilter(与文件中前缀保持一致)


 */


@Activate
public class ConsumeLogFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(ConsumeLogFilter.class);
    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        /* 生成traceId
        ----------------------------------------------------------
         */
        String traceId = TraceIdGenerator.getTraceId();
        if (StringUtils.isBlank(traceId)) {
            TraceIdGenerator.generateTraceId();
        }

        DubboConfiguration dubboConfiguration = ApplicationContextAware4Dubbo.getContext().getBean(DubboConfiguration.class);

        StopWatch stopWatch = StopWatch.createStarted();
        Result result = invoker.invoke(invocation);

        if (result.hasException()) {
            logger.error(MessageFormat.format("dubbo 请求异常:{0},{1}:{2},参数：{3}", result.getException().getMessage(), invoker.getInterface().getName(), invocation.getMethodName(), invocation.getArguments()), result.getException());
            return result;
        }

        stopWatch.stop();
        long milliSeconds = stopWatch.getTime(TimeUnit.MILLISECONDS);

        boolean isNotExclude = true;
        if (dubboConfiguration.getRecordInvokeExcludeMethod() != null) {
            isNotExclude = dubboConfiguration.getRecordInvokeExcludeMethod().stream().filter(x -> x.equalsIgnoreCase(invocation.getMethodName())).count() == 0;
        }

        if (dubboConfiguration.isRecordInvokeResult() && isNotExclude) {
            try {
                String jsonResult = mapper.writeValueAsString(result.getValue());
                logger.warn("dubbo 请求调用，时长:{},{}:{},参数：{},结果：{}", milliSeconds, invoker.getInterface().getName(), invocation.getMethodName(), invocation.getArguments(), jsonResult);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            logger.warn("dubbo 请求调用，时长:{},{}:{},参数：{}", milliSeconds, invoker.getInterface().getName(), invocation.getMethodName(), invocation.getArguments());
        }

        return result;
    }
}
