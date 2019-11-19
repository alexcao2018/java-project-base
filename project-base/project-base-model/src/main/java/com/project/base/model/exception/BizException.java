package com.project.base.model.exception;

public class BizException extends RuntimeException {

    private static final long serialVersionUID = -8279208109267434451L;
    private String code = "999";

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
