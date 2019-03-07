package com.project.base.redis;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

public class RedisClientWrapper {
    private RedisClient redisClient;
    private String flag;

    public RedisClient getRedisClient() {
        return redisClient;
    }

    public void setRedisClient(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}
