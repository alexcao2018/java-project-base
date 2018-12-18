package com.project.base.common.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class HttpTool {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final CloseableHttpClient client = HttpClients.createDefault();

    public static final <T> T post(String url, Map<String, Object> params, Class<T> clazz)
            throws IOException {
        return post(url, params, clazz, 30000);
    }

    public static final <T> T post(String url, Map<String, Object> params, Class<T> clazz, Integer timeout)
            throws IOException {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        CloseableHttpResponse response;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setEntity(generatePostEntity(params));
        response = client.execute(httpPost);
        HttpEntity httpEntity = response.getEntity();
        return MAPPER.readValue(EntityUtils.toString(httpEntity), clazz);
    }

    private static StringEntity generatePostEntity(Map<String, Object> params) throws JsonProcessingException {

        String json = MAPPER.writeValueAsString(params);
        StringEntity stringEntity = new StringEntity(json, "UTF-8");
        stringEntity.setContentEncoding("UTF-8");
        return stringEntity;
    }

    public static final <T> T get(String url, Map<String, String> params, Class<T> clazz)
            throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(url);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.setParameter(entry.getKey(), entry.getValue());
        }
        URI uri2 = builder.build();
        return get(uri2.toString(), clazz, 30000);
    }

    public static final <T> T get(String uri, Class<T> clazz, Integer timeout)
            throws IOException {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        CloseableHttpResponse response;
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setConfig(config);
        response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        return MAPPER.readValue(EntityUtils.toString(entity), clazz);
    }

    public static final <T> T get(String url, Map<String, String> params, Class<T> clazz, Integer timeout)
            throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(url);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.setParameter(entry.getKey(), entry.getValue());
        }
        URI uri2 = builder.build();
        return get(uri2.toString(), clazz, timeout);
    }
}
