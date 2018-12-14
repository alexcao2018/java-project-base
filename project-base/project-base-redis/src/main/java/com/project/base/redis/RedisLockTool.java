package com.project.base.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisLockTool {

    private RedisTemplate<String, Object> redisTemplate;

    private static Logger logger = LoggerFactory.getLogger(RedisLockTool.class);


    private String lockKey;


    private volatile boolean locked = false;

    public RedisLockTool(RedisTemplate<String, Object> redisTemplate, String lockKey) {
        this.redisTemplate = redisTemplate;
        this.lockKey = lockKey + "_lock";
    }

    public String getLockKey() {
        return lockKey;
    }


    public boolean setNX(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute((RedisCallback<Object>) connection -> {
                StringRedisSerializer serializer = new StringRedisSerializer();
                Boolean success = connection.setNX(serializer.serialize(key), serializer.serialize(value));
                connection.expire(serializer.serialize(key), 30);
                connection.close();
                return success;
            });
        } catch (Exception e) {
            logger.error("setNX redis error, key : {}", key);
        }
        return obj != null ? (Boolean) obj : false;
    }

    public synchronized boolean lock() {
        long expires = System.currentTimeMillis();
        String expiresStr = String.valueOf(expires);
        if (this.setNX(lockKey, expiresStr)) {
            locked = true;
            return true;
        }
        return false;
    }

    public synchronized void unlock() {
        if (locked) {
            redisTemplate.delete(lockKey);
            locked = false;
        }
    }


}
