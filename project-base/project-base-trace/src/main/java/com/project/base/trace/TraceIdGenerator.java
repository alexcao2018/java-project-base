package com.project.base.trace;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.UUID;

public class TraceIdGenerator {
    private static TransmittableThreadLocal<String> traceTTL = new TransmittableThreadLocal<>();

    public static String generateTraceId(){
        String traceId = UUID.randomUUID().toString();
        traceTTL.set(traceId);
        return traceId;
    }

    public static String getTraceId(){
        return traceTTL.get();
    }

    public static void removeTraceId(){
        traceTTL.remove();
    }

}
