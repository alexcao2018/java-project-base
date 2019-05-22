package com.project.base.rpc.patch.log.support;

import com.shihang.arch.rpc.URL;
import com.shihang.arch.rpc.session.Configuration;
import com.shihang.arch.rpc.transport.TransportException;
import com.shihang.arch.rpc.transport.defaults.ApacheHttpClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;

public class TimeApacheHttpClientTransport extends ApacheHttpClientTransport {

    private final ByteArrayOutputStream requestBuffer = new ByteArrayOutputStream();

    private static Logger logger = LoggerFactory.getLogger(TimeApacheHttpClientTransport.class);


    public TimeApacheHttpClientTransport(URL url, Configuration configuration) {
        super(url, configuration);
    }

    @Override
    public int read(byte[] buf, int off, int len) throws TransportException {
        return super.read(buf, off, len);
    }

    @Override
    protected void write(byte[] buf, int off, int len) throws TransportException {
        requestBuffer.write(buf, off, len);
        super.write(buf, off, len);
    }

    @Override
    public void flush() throws TransportException {
        URL url = getUrl();
        String uri = url.getHost() + "://" + url.getPort() + url.getPath();
        String parameter = "";
        byte[] data = requestBuffer.toByteArray();
        if (data != null && data.length != 0) {
            parameter = new String(data);
        }

        String httpRequestBegin = MessageFormat.format("rpc 请求开始,url:【{0}】,参数:{1}"
                , uri
                , parameter);

        logger.warn(httpRequestBegin);

        long t = System.currentTimeMillis();
        super.flush();

        String httpRequestEnd = MessageFormat.format("rpc 请求结束,执行时间:{2},url:【{0}】,参数：{1}"
                , uri
                , parameter
                , (System.currentTimeMillis() - t));

        logger.warn(httpRequestEnd);
    }
}
