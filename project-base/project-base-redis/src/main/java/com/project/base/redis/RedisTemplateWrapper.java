package com.project.base.redis;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisTemplateWrapper {
    private RedisTemplate<String, Object> redisTemplate;
    private String flag;

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
