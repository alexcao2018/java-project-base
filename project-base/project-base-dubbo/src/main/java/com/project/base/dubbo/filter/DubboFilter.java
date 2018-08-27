package com.project.base.dubbo.filter;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
/*
    第一步：
       1. 在/resources/META-INF/dubbo 中建立文件
         com.alibaba.dubbo.rpc.Filter
       2. 文件中填写
         dubboFilter=com.project.base.dubbo.filter.DubboFilter


    第二步：

        需要在yml 文件中配置
        dubbo:
          provider:
            loadbalance: leastactive
            retries: 0
            timeout: 1200000
            filter: dubboFilter(与文件中前缀保持一致)
 */


@Activate
public class DubboFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return null;
    }
}
