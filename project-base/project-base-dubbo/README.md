
1、filter 使用
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

2、listener 使用

    第一步：
           1. 在/resources/META-INF/dubbo 中建立文件
             com.alibaba.dubbo.rpc.ExporterListener
           2. 文件中填写
             exporterListener=com.project.base.dubbo.filter.DubboResolveExporterListener
    
    
        第二步：
    
            需要在yml 文件中配置
            dubbo:
              provider:
                exporterListener: dubboExporterListener
