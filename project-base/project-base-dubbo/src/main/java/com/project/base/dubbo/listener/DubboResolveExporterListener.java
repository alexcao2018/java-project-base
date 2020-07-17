package com.project.base.dubbo.listener;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.ExporterListener;
import com.alibaba.dubbo.rpc.RpcException;
import com.project.base.common.lang.SystemTool;
import com.project.base.common.net.NetworkTool;
import com.project.base.dubbo.init.ApplicationContextAware4Dubbo;
import com.project.base.dubbo.init.DubboConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/*
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
 */

@Activate
public class DubboResolveExporterListener implements ExporterListener {

    private Logger logger = LoggerFactory.getLogger(DubboResolveExporterListener.class);

    private DubboConfiguration dubboConfiguration = ApplicationContextAware4Dubbo.getContext().getBean(DubboConfiguration.class);

    @Override
    public void exported(Exporter<?> exporter) throws RpcException {

        if (!SystemTool.isDebug())
            return;

        String dubboPort = dubboConfiguration.getDubboPort();
        String dubboHost = NetworkTool.getLocalAddress().getHostAddress();
        String interfaceName = exporter.getInvoker().getInterface().getName();
        String newResolve = MessageFormat.format("{0}=dubbo://{1}:{2}?default.timeout=1200000", interfaceName, dubboHost, dubboPort);

        String userHome = SystemTool.getUserHome().getAbsolutePath();
        String dubboResolvePath = userHome + "/dubbo-resolve.properties";
        File dubboResolveFile = new File(dubboResolvePath);

        List<String> lineCollection = null;
        if (dubboResolveFile.exists()) {
            try {
                lineCollection = Files.readAllLines(Paths.get(dubboResolvePath));

                boolean isMatch = false;
                for (int i = 0; i < lineCollection.size(); i++) {
                    String line = lineCollection.get(i);
                    String regex = MessageFormat.format("^{0}\\s*=\\s*.+", interfaceName.replaceAll("\\.", "\\\\."));
                    if (line.matches(regex)) {
                        lineCollection.set(i, newResolve);
                        isMatch = true;
                    }
                }
                if (!isMatch)
                    lineCollection.add(newResolve);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            lineCollection = new ArrayList<>();
            lineCollection.add(newResolve);
        }


        PrintWriter writer = null;
        try {
            writer = new PrintWriter(dubboResolvePath, "UTF-8");
            for (String line : lineCollection) {
                writer.write(line);
                writer.write(System.getProperty("line.separator"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        logger.info(MessageFormat.format("添加{0} 到 {1}", newResolve, dubboResolvePath));
    }

    @Override
    public void unexported(Exporter<?> exporter) {

    }
}
