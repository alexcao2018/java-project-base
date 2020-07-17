package com.project.base.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Collections;


public class RedisLock {
    private static Logger logger = LoggerFactory.getLogger(RedisLock.class);
    private static final String luaScript =
            "if (redis.call('GET', KEYS[1]) == ARGV[1]) "
                    + "then return redis.call('DEL',KEYS[1]) "
                    + "else " + "return 0 " + "end";

    private static RedisScript<String> scriptLock = new DefaultRedisScript<String>(luaScript, String.class);

    /**
     * 默认锁过期时间30秒
     *
     * @param redisClient
     * @param key
     * @param value
     * @return
     */
    public static boolean lock(RedisClient redisClient, String key, String value) {
        if (null == key || null == value) {
            return false;
        }
        return redisClient.setIfAbsentAtomic(key, value, 30);
    }

    /**
     * @param redisClient
     * @param key
     * @param value
     * @param expire      秒
     * @return
     */
    public static boolean lock(RedisClient redisClient, String key, String value, Integer expire) {
        if (null == key || null == value) {
            return false;
        }
        return redisClient.setIfAbsentAtomic(key, value, expire);
    }

    public static boolean releaseLock(RedisClient redisClient, String key, String value) {
        if (key == null || value == null) {
            return false;
        }

        Jedis jedis = null;
        try {
            /* 此处Key 与 value 序列化，与lock 的序列化保持一致
            否则，无法删除成功 ps:（KEYS[1]) == ARGV[1] 保持数据一致）
            ---------------------------
             */
            RedisTemplate<String, Object> redisTemplate = redisClient.getRedisTemplate();
            RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();
            byte[] valueByte = valueSerializer.serialize(value);
            jedis = redisClient.getJedisPool().getResource();
            Object result = jedis.eval(luaScript.getBytes(), Arrays.asList(key.getBytes()), Arrays.asList(valueByte));
            return result != null && Long.parseLong(result.toString()) == 1;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (jedis != null)
                jedis.close();
        }

        return false;
    }


}
