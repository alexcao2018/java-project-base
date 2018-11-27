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

    private static final String serializeObject(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    private static final <T> T deserializeObject(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }
}
