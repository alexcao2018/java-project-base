package com.project.base.dotnet.rpc;

public enum OrderDirection {

    None("None", null),

    Asc("Asc", 1),

    Desc("Desc", 2);

    private String key;

    private Integer value;


    OrderDirection(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }
}
