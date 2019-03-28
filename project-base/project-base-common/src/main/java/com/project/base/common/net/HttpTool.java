package com.project.base.common.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.base.common.json.JsonTool;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class HttpTool {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static CloseableHttpClient client = null;

    private static final Logger logger = LoggerFactory.getLogger(HttpTool.class);

    static {
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        PoolingHttpClientConnectionManager cm = null;
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(50);
        client = HttpClients.custom().setConnectionManager(cm).build();
    }

    public static final <T> T post(String url, Map<String, Object> params, Class<T> clazz)
            throws IOException {
        return post(url, params, clazz, 30000, false);
    }

    public static final <T> T post(String url, Map<String, Object> params, Class<T> clazz, Integer timeout
            , boolean logResponse)
            throws IOException {
        T t;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HttpEntity httpEntity = getEntity(url, params, timeout);
        stopWatch.stop();
        if (clazz == String.class) {
            t = (T) EntityUtils.toString(httpEntity);
        } else
            t = MAPPER.readValue(EntityUtils.toString(httpEntity), clazz);
        StringBuilder sb = new StringBuilder(
                String.format("Url:%s参数:%s响应时间:%d", url, JsonTool.serializeNoException(params), stopWatch.getTime())
        );
        if (logResponse)
            sb.append("响应:").append(JsonTool.serializeNoException(t)).toString();
        logger.info(sb.toString());
        return t;
    }

    public static final <T> T post(String url, Map<String, Object> params, TypeReference valueTypeRef, Integer timeout
            , boolean logResponse)
            throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HttpEntity httpEntity = getEntity(url, params, timeout);
        stopWatch.stop();
        T t = MAPPER.readValue(EntityUtils.toString(httpEntity), valueTypeRef);

        StringBuilder sb = new StringBuilder(
                String.format("Url:%s参数:%s响应时间:%d", url, JsonTool.serializeNoException(params), stopWatch.getTime())
        );
        if (logResponse)
            sb.append("响应:").append(JsonTool.serializeNoException(t)).toString();
        logger.info(sb.toString());
        return t;
    }

    private static HttpEntity getEntity(String url, Map<String, Object> params, Integer timeout) throws IOException {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setEntity(generatePostEntity(params));
        try {
            response = client.execute(httpPost);
            return response.getEntity();
        } catch (IOException e) {
            if (response != null)
                response.close();
            httpPost.releaseConnection();
            throw e;
        }

    }


    private static StringEntity generatePostEntity(Map<String, Object> params) throws JsonProcessingException {

        String json = MAPPER.writeValueAsString(params);
        StringEntity stringEntity = new StringEntity(json, "UTF-8");
        stringEntity.setContentEncoding("UTF-8");
        return stringEntity;
    }

    public static final <T> T get(String url, Map<String, String> params, Class<T> clazz
            , boolean logResponse)
            throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(url);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.setParameter(entry.getKey(), entry.getValue());
        }
        URI uri2 = builder.build();
        return get(uri2.toString(), clazz, 30000, logResponse);
    }

    public static final <T> T get(String uri, Class<T> clazz, Integer timeout, boolean logResponse)
            throws IOException {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        CloseableHttpResponse response = null;
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setConfig(config);
        try {
            T t;
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            response = client.execute(httpGet);
            stopWatch.stop();
            HttpEntity entity = response.getEntity();
            if (clazz == String.class)
                t = (T) EntityUtils.toString(entity);
            else
                t = MAPPER.readValue(EntityUtils.toString(entity), clazz);
            StringBuilder sb = new StringBuilder(
                    String.format("Url:%s响应时间:%d", uri, stopWatch.getTime())
            );
            if (logResponse)
                sb.append("响应:").append(JsonTool.serializeNoException(t)).toString();
            logger.info(sb.toString());
            return t;
        } catch (IOException e) {
            if (response != null)
                response.close();
            httpGet.releaseConnection();
            throw e;
        }

    }

    public static final <T> T get(String url, Map<String, String> params, Class<T> clazz, Integer timeout,
                                  boolean logResponse)
            throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(url);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.setParameter(entry.getKey(), entry.getValue());
        }
        URI uri2 = builder.build();
        return get(uri2.toString(), clazz, timeout, logResponse);
    }
}
