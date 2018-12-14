package com.project.base.dubbo.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        StopWatch stopWatch = StopWatch.createStarted();
        Result result = invoker.invoke(invocation);
        stopWatch.stop();
        long milliSeconds = stopWatch.getTime(TimeUnit.MILLISECONDS);
        logger.info("dubbo调用，时长:{},{}:{},参数：{}", milliSeconds, invoker.getInterface().getName(), invocation.getMethodName(), invocation.getArguments());

        return result;
    }
}
