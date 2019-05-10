package com.project.base.common.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.base.common.json.JsonTool;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
        return post(url, params, clazz, 30000, "application/json;charset=UTF-8", false);
    }

    public static final <T> T post(String url, Map<String, Object> params, Class<T> clazz, Integer timeout
            , String contentType, boolean logResponse)
            throws IOException {
        T t;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HttpEntity httpEntity = getEntity(url, params, timeout, contentType);
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
        HttpEntity httpEntity = getEntity(url, params, timeout, "application/json;charset=UTF-8");
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

    private static HttpEntity getEntity(String url, Map<String, Object> params, Integer timeout,
                                        String contentType) throws IOException {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        httpPost.addHeader("Content-Type", contentType);
        if (contentType.toLowerCase().contains("urlencoded")) {
            httpPost.setEntity(generateUrlEncodePostEntity(params));
        } else
            httpPost.setEntity(generateJsonPostEntity(params));
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


    private static StringEntity generateJsonPostEntity(Map<String, Object> params) throws JsonProcessingException {

        String json = MAPPER.writeValueAsString(params);
        StringEntity stringEntity = new StringEntity(json, "UTF-8");
        stringEntity.setContentEncoding("UTF-8");
        return stringEntity;
    }

    private static StringEntity generateUrlEncodePostEntity(Map<String, Object> params) throws JsonProcessingException,
            UnsupportedEncodingException {

        List<NameValuePair> list = new ArrayList<>();
        params.forEach((k, v) -> {
            NameValuePair nameValuePair = new BasicNameValuePair(k, v.toString());
            list.add(nameValuePair);
        });
        StringEntity stringEntity = new UrlEncodedFormEntity(list, "UTF-8");
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
