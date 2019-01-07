package com.project.base.dotnet.rpc.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yangchao.
 * Date: 2018/12/29
 */
public final class DotnetRpcContext {

    private String urlCityValue;

    private Map<String, Object> values;

    public String getUrlCityValue() {
        return urlCityValue;
    }

    public Object getContextValue(String key) {
        return this.values.get(key);
    }

    private DotnetRpcContext(Builder builder) {
        this.urlCityValue = builder.urlCityValue;
        this.values = builder.values;
    }

    public static class Builder {

        private String urlCityValue;

        private Map<String, Object> values = new HashMap<>();

        public Builder urlCityValue(String urlCityValue) {
            this.urlCityValue = urlCityValue;
            return this;
        }

        public Builder addMap(String key, Object value) {
            if (!values.containsKey(key)) {
                this.values.put(key, value);
            }
            return this;
        }

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public DotnetRpcContext build() {
            return new DotnetRpcContext(this);
        }
    }
}
