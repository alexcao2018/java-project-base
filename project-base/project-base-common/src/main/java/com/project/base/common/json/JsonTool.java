package com.project.base.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonTool {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将对象序列化为string
     *
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static final String serialize(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    /**
     * 将json 序列化为 对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static final <T> T deserialize(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }

    public static final String serializeNoException(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            return "Json序列化失败";
        }

    }

}
