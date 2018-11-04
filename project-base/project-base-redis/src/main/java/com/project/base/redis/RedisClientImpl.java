package com.project.base.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisClientImpl implements RedisClient {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置过期时间，秒为单位
     *
     * @param key
     * @param timeout 秒为单位
     * @return
     */
    @Override
    public boolean expire(String key, int timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 检查是否存在Key
     *
     * @param key
     * @return
     */
    @Override
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Set<String> keys(String pattern){
        return redisTemplate.keys(pattern);
    }

    /**
     * 获取key 对应的 value
     *
     * @param key
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(String key) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object object = valueOperations.get(key);
        return (T) object;
    }

    /**
     * 设置 key 值
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public boolean set(String key, Object value) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
        return true;
    }

    /**
     * 设置key值 以及 过期时间，秒为单位
     *
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    @Override
    public boolean set(String key, Object value, int timeout) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, timeout, TimeUnit.SECONDS);
        return true;
    }

    /**
     * 删除 key
     *
     * @param key
     * @return
     */
    @Override
    public boolean del(String... key) {
        redisTemplate.delete(Arrays.asList(key));
        return true;
    }

    /**
     * hash 设置 key
     *
     * @param key
     * @param hashKey
     * @param value
     * @return
     */
    @Override
    public boolean hSet(String key, Object hashKey, Object value) {
        return hSet(key, hashKey, value, null);
    }

    /**
     * hash 设置 key ,一次性设置多个hash key
     *
     * @param key
     * @param map
     * @return
     */
    @Override
    public boolean hSet(String key, Map<Object, Object> map) {
        return hSet(key, map, null);
    }

    /**
     * hash 设置 key , 以及过期时间，秒为单位
     *
     * @param key
     * @param hashKey
     * @param value
     * @param timeout
     * @return
     */
    @Override
    public boolean hSet(String key, Object hashKey, Object value, Integer timeout) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
        if (timeout != null)
            expire(key, timeout);
        return true;
    }

    /**
     * hash 设置 key ,一次性设置多个hash key 以及过期时间
     *
     * @param key
     * @param map
     * @param timeout
     * @return
     */
    @Override
    public boolean hSet(String key, Map<Object, Object> map, Integer timeout) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.putAll(key, map);
        if (timeout != null)
            expire(key, timeout);
        return true;
    }

    /**
     * hash 获取 hashkey 对应值
     *
     * @param key
     * @param hashKey
     * @param <T>
     * @return
     */
    @Override
    public <T> T hGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Object value = hash.get(key, hashKey);
        return (T) value;
    }

    /**
     * 获取所有hashkey 值
     *
     * @param key
     * @return
     */
    @Override
    public Map<Object, Object> hGetAll(String key) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Map<Object, Object> all = hash.entries(key);
        return all;
    }

    /**
     * 获取多个hashkey 值
     *
     * @param key
     * @param hashKeys
     * @return
     */
    @Override
    public List<Object> hGetMultiple(String key, Object... hashKeys) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        List<Object> objectCollection = hash.multiGet(key, Arrays.asList(hashKeys));
        return objectCollection;
    }

    /**
     * 删除多个hashkey
     *
     * @param key
     * @param hashKeys
     * @return
     */
    @Override
    public long hDel(String key, Object... hashKeys) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        long result = hash.delete(key, hashKeys);
        return result;
    }

    /**
     * 是否存在hashkey值
     *
     * @param key
     * @param hashKey
     * @return
     */
    @Override
    public boolean hExists(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        boolean hasKey = hash.hasKey(key, hashKey);
        return hasKey;
    }


    /**
     * 获取所有hashkey集合
     *
     * @param key
     * @return
     */
    @Override
    public Set<Object> hKeys(String key) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Set<Object> set = hash.keys(key);
        return set;
    }

    /**
     * 从左面向列表push 元素
     * @param key
     * @param value
     * @return
     */
    @Override
    public long lLeftPush(String key, Object value) {
        return lLeftPush(key, value, null);
    }

    /**
     * 从左面向列表push 元素 以及过期时间，秒为单位
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    @Override
    public long lLeftPush(String key, Object value, Integer timeout) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        long result = listOperations.leftPush(key, value);
        if (timeout != null)
            expire(key, timeout);
        return result;
    }

    /**
     * 从左面向列表push 元素
     * @param key
     * @param values
     * @return
     */
    @Override
    public long lLeftPush(String key, Object... values) {
        return lLeftPush(key, null, values);
    }

    /**
     * 从左面向列表push 元素 以及过期时间，秒为单位
     * @param key
     * @param timeout
     * @param values
     * @return
     */
    @Override
    public long lLeftPush(String key, Integer timeout, Object... values) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        long result = listOperations.leftPushAll(key, values);
        if (timeout != null)
            expire(key, timeout);
        return result;
    }

    /**
     * 从右面向列表push 元素 以及过期时间
     * @param key
     * @param value
     * @return
     */
    @Override
    public long lRightPush(String key, Object value) {
        return lRightPush(key, value, null);
    }

    /**
     * 从右面向列表push 元素 以及过期时间，秒为单位
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    @Override
    public long lRightPush(String key, Object value, Integer timeout) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        long result = listOperations.rightPush(key, value);
        if (timeout != null)
            expire(key, timeout);
        return result;
    }

    /**
     * 从右面向列表push 元素
     * @param key
     * @param values
     * @return
     */
    @Override
    public long lRightPush(String key, Object... values) {
        return lRightPush(key, null, values);
    }

    /**
     * 从右面向列表push 元素 以及过期时间，秒为单位
     * @param key
     * @param timeout
     * @param values
     * @return
     */
    @Override
    public long lRightPush(String key, Integer timeout, Object... values) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        long result = listOperations.rightPushAll(key, values);
        if (timeout != null)
            expire(key, timeout);
        return result;
    }

    @Override
    public <T> T lLeftPop(String key) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        Object value = listOperations.leftPop(key);
        return (T) value;
    }

    @Override
    public <T> T lRightPop(String key) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        Object value = listOperations.rightPop(key);
        return (T) value;
    }

    @Override
    public <T> T lIndex(String key, long index) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        Object value = listOperations.index(key, index);
        return (T) value;
    }

    @Override
    public <T> List<T> lRange(String key, long start, long end) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        List<Object> value = listOperations.range(key, start, end);
        return (List<T>) value;
    }

    @Override
    public long lSize(String key) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        long size = listOperations.size(key);
        return size;
    }


    /**
     * Lrem 命令
     * COUNT 的值可以是以下几种：
     * <p>
     * count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
     * count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
     * count = 0 : 移除表中所有与 VALUE 相等的值。
     *
     * @param key
     * @param count
     * @param value
     * @return
     */
    @Override
    public long lDel(String key, long count, Object value) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        long result = listOperations.remove(key, count, value);
        return result;
    }

    @Override
    public boolean lTrim(String key, long start, long end) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        listOperations.trim(key, start, end);
        return true;
    }

    @Override
    public boolean sAdd(String key, Object... values) {
        return sAdd(key, null, values);
    }

    @Override
    public boolean sAdd(String key, Integer timeout, Object... values) {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        setOperations.add(key, values);
        if (timeout != null)
            expire(key, timeout);
        return true;
    }

    @Override
    public <T> Set<T> sMembers(String key) {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        return (Set<T>) setOperations.members(key);
    }

    @Override
    public long sSize(String key) {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        return setOperations.size(key);
    }

    @Override
    public <T> Set<T> sDiff(String key, String... otherKeys) {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        return (Set<T>) setOperations.difference(key, Arrays.asList(otherKeys));
    }

    @Override
    public <T> Set<T> sIntersect(String key, String... otherKeys) {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        return (Set<T>) setOperations.intersect(key, Arrays.asList(otherKeys));
    }

    @Override
    public <T> Set<T> sUnion(String key, String... otherKeys) {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        return (Set<T>) setOperations.union(key, Arrays.asList(otherKeys));
    }

    @Override
    public long sDel(String key, Object... values) {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        return setOperations.remove(key, values);
    }

    @Override
    public <T> T sPop(String key) {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        return (T) setOperations.pop(key);
    }

    @Override
    public boolean sIsMember(String key, Object object) {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        return setOperations.isMember(key, object);
    }


    @Override
    public long zAdd(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        return zAdd(key, null, tuples);
    }

    @Override
    public long zAdd(String key, Integer timeout, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        long result = zSetOperations.add(key, tuples);
        if (timeout != null)
            expire(key, timeout);
        return result;
    }

    @Override
    public boolean zAdd(String key, Object value, double score) {
        return zAdd(key, null, value, score);
    }

    @Override
    public boolean zAdd(String key, Integer timeout, Object value, double score) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(key, value, score);
        if (timeout != null)
            expire(key, timeout);
        return true;
    }

    @Override
    public long zCount(String key, double min, double max) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.count(key, min, max);
    }

    @Override
    public long zSize(String key) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.size(key);
    }

    /**
     * Redis Zremrangebyrank 命令用于移除有序集中，指定排名(rank)区间内的所有成员。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public long zDelRangeByRank(String key, long start, long end) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.removeRange(key, start, end);
    }

    @Override
    public long zDelRangeByScore(String key, double min, double max) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.removeRangeByScore(key, min, max);
    }

    @Override
    public long zDel(String key, Object... values) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.remove(key, values);
    }

    /**
     * Determine the index of element with {@code value} in a sorted set.
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public long zRank(String key, Object value) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.rank(key, value);
    }

    @Override
    public long zReverseRank(String key, Object value) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.reverseRank(key, value);
    }

    @Override
    public Double zScore(String key, Object value) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.score(key, value);
    }

    @Override
    public <T> Set<T> zRangeByRank(String key, long start, long end) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return (Set<T>) zSetOperations.range(key, start, end);
    }

    @Override
    public <T> Set<T> zRangeByScore(String key, double min, double max) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return (Set<T>) zSetOperations.rangeByScore(key, min, max);
    }

    @Override
    public <T> Set<T> zRangeByScore(String key, double min, double max, long offset, long count) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return (Set<T>) zSetOperations.rangeByScore(key, min, max, offset, count);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.rangeByScoreWithScores(key, min, max, offset, count);
    }

    @Override
    public <T> Set<T> zRangeByLex(String key, RedisZSetCommands.Range rang) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return (Set<T>) zSetOperations.rangeByLex(key, rang);
    }

    @Override
    public <T> Set<T> zRangeByLex(String key, RedisZSetCommands.Range rang, RedisZSetCommands.Limit limit) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return (Set<T>) zSetOperations.rangeByLex(key, rang, limit);
    }

    @Override
    public <T> Set<T> zReverseRangeByRank(String key, long start, long end) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return (Set<T>) zSetOperations.reverseRange(key, start, end);
    }

    @Override
    public <T> Set<T> zReverseRangeByScore(String key, double min, double max) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return (Set<T>) zSetOperations.reverseRangeByScore(key, min, max);
    }

    @Override
    public <T> Set<T> zReverseRangeByScore(String key, double min, double max, long offset, long count) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return (Set<T>) zSetOperations.reverseRangeByScore(key, min, max, offset, count);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(String key, double min, double max) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.reverseRangeByScoreWithScores(key, min, max);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.reverseRangeByScoreWithScores(key, min, max, offset, count);
    }

}
