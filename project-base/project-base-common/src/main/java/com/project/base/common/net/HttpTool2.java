package com.project.base.common.net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.base.common.enums.EnumContentType;
import com.project.base.common.json.JsonTool;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpTool2 {

    private static Logger logger = LoggerFactory.getLogger(HttpTool2.class);
    private static final int CONNECT_TIME_OUT = 3 * 1000;
    private static final int CONNECTION_REQUEST_TIME_OUT = 3 * 1000;
    private static final int SOCKET_TIME_OUT = 3 * 1000;


    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
    private static CloseableHttpClient httpClient;

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        poolingHttpClientConnectionManager.setMaxTotal(200);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(200);
        httpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
    }

    /**
     * 超时时间：3秒
     *
     * @param url                  请求url
     * @param clazzOrTypeReference 返回单个对象 , 传参：Test.class , 返回集合, 传参：new TypeReference<List<Test>>() {}
     * @param <T>
     * @return
     */
    public static <T> T get(String url, Object clazzOrTypeReference) {
        return get(url, null, null, SOCKET_TIME_OUT, true, clazzOrTypeReference);
    }

    /**
     * 超时时间：3秒
     *
     * @param url
     * @param urlParameterMap      请求query string 参数
     * @param clazzOrTypeReference 返回单个对象 , 传参：Test.class , 返回集合, 传参：new TypeReference<List<Test>>() {}
     * @param <T>
     * @return
     */
    public static <T> T get(String url, Map<String, String> urlParameterMap, Object clazzOrTypeReference) {
        return get(url, urlParameterMap, null, SOCKET_TIME_OUT, true, clazzOrTypeReference);
    }


    /**
     * @param url                  请求url
     * @param urlParameterMap      请求query string 参数
     * @param timeout              超时时间 , 单位：毫秒
     * @param isLogResponse        是否记录返回结果
     * @param clazzOrTypeReference 返回单个对象 , 传参：Test.class , 返回集合, 传参：new TypeReference<List<Test>>() {}
     * @param <T>
     * @return
     */
    public static <T> T get(String url, Map<String, String> urlParameterMap, Integer timeout, boolean isLogResponse, Object clazzOrTypeReference) {
        return get(url, urlParameterMap, null, timeout, isLogResponse, clazzOrTypeReference);
    }


    /**
     * @param url                  请求url
     * @param urlParameterMap      请求query string 参数
     * @param requestHeader        请求header 如果为null 则不处理
     * @param timeout              超时时间 , 单位：毫秒
     * @param isLogResponse        是否记录返回结果
     * @param clazzOrTypeReference 返回单个对象 , 传参：Test.class , 返回集合, 传参：new TypeReference<List<Test>>() {}
     * @param <T>
     * @return
     */
    public static <T> T get(String url, Map<String, String> urlParameterMap, Map<String, String> requestHeader, Integer timeout, boolean isLogResponse, Object clazzOrTypeReference) {
        /* config request
        ----------------------------
         */
        RequestConfig requestConfig = buildRequestConfig(timeout);

        /* add request parameter to url
        ----------------------------
         */
        url = buildUrl(url, urlParameterMap);

        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        if (requestHeader != null) {
            for (String key : requestHeader.keySet()) {
                request.addHeader(key, requestHeader.get(key));
            }
        }

        T result = executeRequest(request, isLogResponse, clazzOrTypeReference);
        return result;
    }

    /**
     * 通过get  下载内容到byte[]
     *
     * @param url
     * @param timeout
     * @return
     */
    public static byte[] getToByteArray(String url, Integer timeout) {
        CloseableHttpResponse response = null;
        HttpGet request = new HttpGet(url);
        RequestConfig requestConfig = buildRequestConfig(timeout);
        request.setConfig(requestConfig);
        byte[] byteArray = executeRequest(request, StringUtils.EMPTY);
        return byteArray;
    }


    /**
     * @param url
     */
    public static void post(String url) {
        post(url, null, null, null, EnumContentType.Json, SOCKET_TIME_OUT, true, null);
    }

    /**
     * @param url                  请求url
     * @param clazzOrTypeReference 返回单个对象 , 传参：Test.class , 返回集合, 传参：new TypeReference<List<Test>>() {}
     * @param <T>
     * @return
     */
    public static <T> T post(String url, Object clazzOrTypeReference) {
        return post(url, null, null, null, EnumContentType.Json, SOCKET_TIME_OUT, true, clazzOrTypeReference);
    }


    /**
     * @param url                  请求url
     * @param requestBody          请求体，map 或者 对象实例，将进行json序列化到请求体
     * @param clazzOrTypeReference 返回单个对象 , 传参：Test.class , 返回集合, 传参：new TypeReference<List<Test>>() {}
     * @param <T>
     * @return
     */
    public static <T> T post(String url, Object requestBody, Object clazzOrTypeReference) {
        return post(url, null, null, requestBody, EnumContentType.Json, SOCKET_TIME_OUT, true, clazzOrTypeReference);
    }

    /**
     * @param url                  请求url
     * @param urlParameterMap      拼接url 后面的key=value
     * @param requestBody          请求体，map 或者 对象实例，将进行json序列化到请求体
     * @param clazzOrTypeReference 返回单个对象 , 传参：Test.class , 返回集合, 传参：new TypeReference<List<Test>>() {}
     * @param <T>
     * @return
     */
    public static <T> T post(String url, Map<String, String> urlParameterMap, Object requestBody, Object clazzOrTypeReference) {
        return post(url, urlParameterMap, null, requestBody, EnumContentType.Json, SOCKET_TIME_OUT, true, clazzOrTypeReference);
    }

    /**
     * @param url                  请求url
     * @param urlParameterMap      拼接url 后面的key=value
     * @param requestBody          请求体，map 或者 对象实例，将进行json序列化到请求体
     * @param timeout              超时时间 , 单位：毫秒
     * @param clazzOrTypeReference 返回单个对象 , 传参：Test.class , 返回集合, 传参：new TypeReference<List<Test>>() {}
     * @param <T>
     * @return
     */
    public static <T> T post(String url, Map<String, String> urlParameterMap, Object requestBody, Integer timeout, Object clazzOrTypeReference) {
        return post(url, urlParameterMap, null, requestBody, EnumContentType.Json, timeout, true, clazzOrTypeReference);
    }

    /**
     * @param url                  请求url
     * @param urlParameterMap      拼接url 后面的key=value
     * @param requestBody          请求体
     * @param contentType          请求内容类型 application/json or application/x-www-urlencoded
     * @param timeout              超时时间 , 单位：毫秒
     * @param isLogResponse        是否记录请求返回
     * @param clazzOrTypeReference 返回单个对象 , 传参：Test.class , 返回集合, 传参：new TypeReference<List<Test>>() {}
     * @param <T>
     * @return
     */
    public static <T> T post(String url, Map<String, String> urlParameterMap, Object requestBody, EnumContentType contentType, Integer timeout, boolean isLogResponse, Object clazzOrTypeReference) {
        return post(url, urlParameterMap, null, requestBody, contentType, timeout, isLogResponse, clazzOrTypeReference);
    }

    /**
     * @param url                  请求url
     * @param urlParameterMap      拼接url 后面的key=value
     * @param requestHeader        请求header 如果为null 则不处理
     * @param requestBody          请求体
     * @param contentType          请求内容类型 application/json or application/x-www-urlencoded
     * @param timeout              超时时间 , 单位：毫秒
     * @param isLogResponse        是否记录请求返回
     * @param clazzOrTypeReference 返回单个对象 , 传参：Test.class , 返回集合, 传参：new TypeReference<List<Test>>() {}
     * @param <T>
     * @return
     */
    public static <T> T post(String url, Map<String, String> urlParameterMap, Map<String, String> requestHeader, Object requestBody, EnumContentType contentType, Integer timeout, boolean isLogResponse, Object clazzOrTypeReference) {
        /* config request
        ----------------------------
         */
        RequestConfig requestConfig = buildRequestConfig(timeout);

        /* add request parameter to url
        ----------------------------
         */
        url = buildUrl(url, urlParameterMap);

         /*
        ----------------------------
         */
        HttpPost request = buildHttpPost(url, requestConfig, contentType, requestHeader, requestBody);
        String jsonBody = StringUtils.EMPTY;
        if (requestBody != null)
            jsonBody = JsonTool.serializeNoException(requestBody);

        T result = executeRequest(request, jsonBody, isLogResponse, clazzOrTypeReference);
        return result;
    }


    /**
     * 通过post 下载内容到byte[]
     *
     * @param url
     * @param requestBody
     * @param timeout
     * @return
     */
    public static byte[] postToByteArray(String url, Object requestBody, Integer timeout) {
        return postToByteArray(url, null, null, requestBody, EnumContentType.Json, timeout, true);
    }


    /**
     * 通过post 下载内容到byte[]
     *
     * @param url
     * @param urlParameterMap
     * @param requestHeader
     * @param requestBody
     * @param contentType
     * @param timeout
     * @param isLogResponse
     * @return
     */
    public static byte[] postToByteArray(String url, Map<String, String> urlParameterMap, Map<String, String> requestHeader, Object requestBody, EnumContentType contentType, Integer timeout, boolean isLogResponse) {
        /* config request
        ----------------------------
         */
        RequestConfig requestConfig = buildRequestConfig(timeout);

        /* add request parameter to url
        ----------------------------
         */
        url = buildUrl(url, urlParameterMap);

        HttpPost request = buildHttpPost(url, requestConfig, contentType, requestHeader, requestBody);

        String jsonBody = StringUtils.EMPTY;
        if (requestBody != null)
            jsonBody = JsonTool.serializeNoException(requestBody);

        byte[] result = executeRequest(request, jsonBody);
        return result;
    }


    /**
     * @param request
     * @param isLogResponse
     * @param clazzOrTypeReference
     * @param <T>
     * @return
     */
    private static <T> T executeRequest(HttpUriRequest request, boolean isLogResponse, Object clazzOrTypeReference) {
        return executeRequest(request, StringUtils.EMPTY, isLogResponse, clazzOrTypeReference);
    }

    /**
     * 执行请求
     *
     * @param request
     * @param isLogResponse
     * @param clazzOrTypeReference
     * @param <T>
     * @return
     */
    private static <T> T executeRequest(HttpUriRequest request, String requestBody, boolean isLogResponse, Object
            clazzOrTypeReference) {
        CloseableHttpResponse response = null;

        T result = null;
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            response = httpClient.execute(request);
            stopWatch.stop();

            StringBuilder sb = new StringBuilder(MessageFormat.format("请求Url:【{0}】,请求体：【{1}】,响应时间:{2}毫秒", request.getURI().toString(), requestBody, stopWatch.getTime()));
            HttpEntity httpEntity = response.getEntity();
            String responseStr = EntityUtils.toString(httpEntity, Charset.forName("UTF-8"));
            if (isLogResponse) {
                sb.append(",响应内容:").append(responseStr);
            }
            logger.info(sb.toString());
            if (clazzOrTypeReference != null && clazzOrTypeReference instanceof Class) {
                result = clazzOrTypeReference == String.class ? (T) responseStr : objectMapper.readValue(responseStr, (Class<T>) clazzOrTypeReference);
            } else if (clazzOrTypeReference != null && clazzOrTypeReference instanceof TypeReference) {
                result = objectMapper.readValue(responseStr, (TypeReference) clazzOrTypeReference);
            }
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder(MessageFormat.format("请求异常,请求Url:【{0}】,请求体：【{1}】,响应时间:{2}毫秒。", request.getURI().toString(), requestBody, stopWatch.getTime()));
            logger.error(sb +"异常堆栈：" +e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return result;
    }

    /**
     * 执行请求 返回byte[]
     *
     * @param request
     * @param requestBody 只是做日志记录
     * @return
     */
    private static byte[] executeRequest(HttpUriRequest request, String requestBody) {
        CloseableHttpResponse response = null;
        byte[] byteArray = null;
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            response = httpClient.execute(request);
            stopWatch.stop();

            StringBuilder sb = new StringBuilder(MessageFormat.format("请求Url:【{0}】,请求体：【{1}】,响应时间:{2}毫秒", request.getURI().toString(), requestBody, stopWatch.getTime()));
            logger.info(sb.toString());

            HttpEntity httpEntity = response.getEntity();
            byteArray = IOUtils.toByteArray(httpEntity.getContent());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return byteArray;
    }

    /**
     * @param url
     * @param requestConfig
     * @param contentType
     * @param requestHeader
     * @param requestBody
     * @return
     */
    private static HttpPost buildHttpPost(String url
            , RequestConfig requestConfig
            , EnumContentType contentType
            , Map<String, String> requestHeader
            , Object requestBody) {
        HttpPost request = new HttpPost(url);
        request.setConfig(requestConfig);
        request.addHeader("Content-Type", contentType.getName());
        if (requestHeader != null) {
            for (String key : requestHeader.keySet()) {
                request.addHeader(key, requestHeader.get(key));
            }
        }

        /* write body to request
        ----------------------------
         */
        if (requestBody != null) {
            switch (contentType) {
                case Json:
                    try {
                        String jsonBody = objectMapper.writeValueAsString(requestBody);
                        StringEntity stringEntity = new StringEntity(jsonBody, StandardCharsets.UTF_8.name());
                        request.setEntity(stringEntity);
                    } catch (JsonProcessingException e) {
                        logger.error(e.getMessage(), e);
                    }
                    break;
                case XWwwFormUrlEncoded:
                    if (!(requestBody instanceof Map)) {
                        throw new RuntimeException("request body must be Map<String,String> instance.");
                    }
                    try {
                        List<NameValuePair> nameValuePairCollection = new ArrayList<>();
                        Map<String, String> stringMapBody = (Map<String, String>) requestBody;
                        stringMapBody.forEach((k, v) -> {
                            NameValuePair nameValuePair = new BasicNameValuePair(k, v);
                            nameValuePairCollection.add(nameValuePair);
                        });
                        StringEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairCollection, StandardCharsets.UTF_8.name());
                        request.setEntity(urlEncodedFormEntity);
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage(), e);
                    }
                    break;
            }
        }

        return request;
    }


    /**
     * 获取默认request config
     *
     * @param timeout
     * @return
     */
    private static RequestConfig buildRequestConfig(Integer timeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIME_OUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT)
                .setSocketTimeout(timeout == null ? SOCKET_TIME_OUT : timeout).build();
        return requestConfig;
    }

    /**
     * @param url
     * @param urlParameterMap
     * @return
     */
    private static String buildUrl(String url, Map<String, String> urlParameterMap) {
        if (urlParameterMap == null)
            return url;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            for (String key : urlParameterMap.keySet()) {
                Object value = urlParameterMap.get(key);
                if (value != null) {
                    uriBuilder.setParameter(key, value.toString());
                }
            }
            url = uriBuilder.build().toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return url;
    }


    public static void setMaxTotal(int max) {
        poolingHttpClientConnectionManager.setMaxTotal(max);
    }

    public static void setDefaultMaxPerRoute(int max) {
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(max);
    }

    public static PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
        return poolingHttpClientConnectionManager;
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    /*
    public static void main(String[] args) {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("a", "a");
        requestMap.put("b", "b");

        BlockingDeque blockingDeque = new LinkedBlockingDeque();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 100, 10, TimeUnit.MINUTES, blockingDeque);

        for (int i = 0; i < 100000; i++) {

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Map<String,Object> stringMap = new HashMap<>();
                    stringMap.put("age","10");
                    stringMap.put("name","ben");
                    stringMap.put("height","111.23");
                    TestBody testBody = new TestBody();
                    testBody.setAge(10);
                    testBody.setName("Ben");
                    testBody.setHeight(new BigDecimal("171.12"));
                    String str = HttpTool2.post("http://10.1.44.65:10001/post", requestMap, stringMap, EnumContentType.XWwwFormUrlEncoded, 500, true, String.class);
                    System.out.println(HttpTool2.getPoolingHttpClientConnectionManager().getTotalStats());
                }
            });

        }
    }*/


}
