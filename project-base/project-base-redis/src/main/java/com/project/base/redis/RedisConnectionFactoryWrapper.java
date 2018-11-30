package com.project.base.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;

public class RedisConnectionFactoryWrapper {
    private RedisConnectionFactory redisConnectionFactory;
    private String flag;

    public RedisConnectionFactory getRedisConnectionFactory() {
        return redisConnectionFactory;
    }

    public void setRedisConnectionFactory(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
