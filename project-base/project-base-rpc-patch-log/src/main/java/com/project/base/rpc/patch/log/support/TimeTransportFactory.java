package com.project.base.rpc.patch.log.support;

import com.shihang.arch.rpc.URL;
import com.shihang.arch.rpc.session.Configuration;
import com.shihang.arch.rpc.transport.ITransport;
import com.shihang.arch.rpc.transport.defaults.ApacheHttpTransportFactory;
import org.springframework.stereotype.Component;

/**
 * 使用该类需要
 * 1，ComponentScan中加上该类的扫描
 * 2，Mapper注解中指定transportFactory
 * transportFactory = TimeTransportFactory.class
 */
@Component
public class TimeTransportFactory extends ApacheHttpTransportFactory  {

    @Override
    public ITransport getTransport(URL url, Configuration configuration) {
        return new TimeApacheHttpClientTransport(url, configuration);
    }

}
