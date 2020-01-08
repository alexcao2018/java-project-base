
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
                
3、dubbo 分批调用
    
    第一步：
        启动类上加：
        @EnableDubboBatchInvoke("com.shihang")
    
    第二步：
    
        定义接口：
         
         @DubboBatchInvoke
         public interface IDubboInterfaceProxy extends IDubboInterface {
             @DubboBatchOption(batchCount = 1, batchParameterPosition = 0)
             void listLimit(List<Long> idCollection, IDubboBatchCallback callback);
         }
         
         说明：
         第一种场景：dubbo 服务返回值 不是list集合，而是包裹在response中，response中有集合数据
             1、将要分批调用的方法签名copy过来，将返回值设置为void，如果需要支持回调，组装数据，则使用IDubboBatchCallback接口
             2、IDubboBatchCallback 接口有exception 参数，如果调用dubbo服务过程中失败，会有exception
                所有需要自己判断是否有exception
         第二种场景：dubbo 服务返回值是 list集合
             1、可以使用第一种场景方式
             2、可以使用正常调用，方法签名和原接口一致，进行override，DubboBatchOption 中 mergeResult 设置为true
4、记录日志
    默认会记录dubbo调用日志 并且会记录调用结果，由于某些方法返回结果过多，记录日志会造成性能问题，
    
    dubbo:
      log:
        logResponse: true  // 是否记录结果         
        
