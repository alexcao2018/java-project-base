package com.project.base.model.exception;

import org.slf4j.event.Level;

public class BizException extends RuntimeException {

    private static final long serialVersionUID = -8279208109267434451L;
    private String code = "999";
    private Level level;

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable e) {
        super(message, e);
    }

    public BizException(String message, String code) {
        super(message);
        this.code = code;
    }

    public BizException(String message, String code, Level level) {
        super(message);
        this.code = code;
        this.level = level;
    }

    public BizException(String message, String code, Throwable e) {
        super(message, e);
        this.code = code;
    }

    public BizException(String message, String code, Throwable e, Level level) {
        super(message, e);
        this.code = code;
        this.level = level;
    }

    public String getCode() {
        return code;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
