package com.project.base.model.exception;

public class BizException extends RuntimeException {

    private String code;

    public BizException(String message) {
        super(message);
    }

    public BizException(String message,Throwable e) {
        super(message,e);
    }

    public BizException(String message, String code) {
        super(message);
        this.code = code;
    }

    public BizException(String message, String code,Throwable e) {
        super(message,e);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
