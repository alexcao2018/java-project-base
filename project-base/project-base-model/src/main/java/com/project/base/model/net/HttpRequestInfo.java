package com.project.base.model.net;

import java.io.Serializable;
import java.text.MessageFormat;

public class HttpRequestInfo implements Serializable {

    private static final long serialVersionUID = 653981130385559297L;

    private String url;
    private String uri;
    private String method;
    private String body;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {

        String requestInfo = MessageFormat.format("{0},请求url:{1},{2}"
                , method
                , url
                , body == null ? "" : ",请求体:" + body);

        return requestInfo;
    }
}
