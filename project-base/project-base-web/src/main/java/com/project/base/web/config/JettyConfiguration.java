package com.project.base.web.config;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @description:
 * @author: Da.Cong
 * @create: 2020-02-07 17:28
 **/
@Configuration
public class JettyConfiguration {

    private Logger logger = LoggerFactory.getLogger(JettyConfiguration.class);

    @Primary
    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory(
            @Value("${server.port:8080}") final int port
            , @Value("${server.jetty.min-threads:8}") final int minThreads
            , @Value("${server.jetty.max-threads:200}") final int maxThreads
            , @Value("${server.jetty.idle-timeout:60000}") final int idleTimeout
            , @Value("${server.jetty.connector-idle-timeout:360}") final int connectorIdleTimeout) {

        final JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();


        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(maxThreads);
        threadPool.setMinThreads(minThreads);
        threadPool.setIdleTimeout(idleTimeout);
        threadPool.setDetailedDump(false);

        factory.setThreadPool(threadPool);
        factory.addServerCustomizers((x) -> jettyServer(x, factory, connectorIdleTimeout));

        logger.info("config JettyEmbeddedServletContainerFactory minThread:{},maxThread:{},idleTimeout:{},connectorIdleTimeout:{}", minThreads, maxThreads, idleTimeout, connectorIdleTimeout);

        return factory;
    }

    private Server jettyServer(Server server, JettyEmbeddedServletContainerFactory factory, final int connectorIdleTimeout) {
        /*
        HandlerCollection handlers = new HandlerCollection();
        for (Handler handler : server.getHandlers()) {
            handlers.addHandler(handler);
        }
        RequestLogHandler reqLogs = new RequestLogHandler();
        NCSARequestLog reqLogImpl = new NCSARequestLog("E:\\jetty\\access-yyyy_mm_dd.log");
        reqLogImpl.setRetainDays(30);
        reqLogImpl.setAppend(true);
        reqLogImpl.setExtended(false);
        reqLogImpl.setLogTimeZone("GMT");
        reqLogs.setRequestLog(reqLogImpl);
        handlers.addHandler(reqLogs);
        server.setHandler(handlers);

        LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
        lowResourcesMonitor.setPeriod(1000);
        lowResourcesMonitor.setLowResourcesIdleTimeout(200);
        lowResourcesMonitor.setMonitorThreads(true);
        lowResourcesMonitor.setMaxConnections(0);
        lowResourcesMonitor.setMaxMemory(1024 * 1024);
        lowResourcesMonitor.setMaxLowResourcesTime(5000);
        server.addBean(lowResourcesMonitor);*/

        //ServerConnector connector = new ServerConnector(server);
        //connector.setPort(8080);

        //connector.setIdleTimeout(idleTimeoutInMinutes * 60 * 1000);


        //server.addConnector(connector);
        //connector.getIdleTimeout();
        Connector connector = server.getConnectors().length > 0 ? server.getConnectors()[0] : null;
        if (connector != null && connector instanceof ServerConnector) {
            ((ServerConnector) connector).setIdleTimeout(connectorIdleTimeout * 1000);
        }
        return server;
    }

}
