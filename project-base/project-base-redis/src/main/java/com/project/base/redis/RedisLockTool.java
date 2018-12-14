package com.project.base.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisLockTool {

    private RedisClient redisClient;
    private String lockKey;

    private volatile boolean locked = false;

    public RedisLockTool(RedisClient redisClient, String lockKey) {
        this.redisClient = redisClient;
        this.lockKey = lockKey + "_lock";
    }

    public String getLockKey() {
        return lockKey;
    }


    public boolean setNX(final String key, final String value) {
        return redisClient.setIfAbsent(key, value, 30);
    }

    public boolean lock() {
        long expires = System.currentTimeMillis();
        String expiresStr = String.valueOf(expires);
        if (this.setNX(lockKey, expiresStr)) {
            locked = true;
            return true;
        }
        return false;
    }

    public void unlock() {
        if (locked) {
            redisClient.del(lockKey);
            locked = false;
        }
    }


}