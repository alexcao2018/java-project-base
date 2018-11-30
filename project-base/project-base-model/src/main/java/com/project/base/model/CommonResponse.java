package com.project.base.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CommonResponse<T> implements Serializable {
    @JsonProperty("Error")
    private Integer error;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Data")
    private T data;
    @JsonProperty("Exception")
    private String exception;

    public static CommonResponse success(Object data) {
        CommonResponse response = new CommonResponse();
        response.setData(data);
        response.setError(0);
        return response;
    }

    public static CommonResponse error(Integer errorcode, String message, String exception) {
        CommonResponse response = new CommonResponse();
        response.setMessage(message);
        response.setException(exception);
        response.setError(errorcode);
        return response;
    }

    public static CommonResponse error(String message) {
        return error(1, message, null);
    }

    public static CommonResponse error(String message, String exception) {
        return error(1, message, exception);
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}

