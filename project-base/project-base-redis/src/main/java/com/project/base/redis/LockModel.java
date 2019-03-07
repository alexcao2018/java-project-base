package com.project.base.redis;

import java.io.Serializable;

public class LockModel implements Serializable {
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
