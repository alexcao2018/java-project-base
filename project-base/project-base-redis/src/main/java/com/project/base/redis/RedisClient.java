package com.project.base.redis;

import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisClient {

    /** 通用数据类型接口
    -------------------------------------------
     */
    boolean set(String key, Object value);

    boolean set(String key, Object value, int timeout);

    boolean del(String... key);

    boolean expire(String key, int timeout);

    boolean hasKey(String key);

    <T> T get(String key);

    /** hash 数据类型接口
    -------------------------------------------
     */

    boolean hSet(String key, Object hashKey, Object value);

    boolean hSet(String key, Map<Object, Object> map);

    boolean hSet(String key, Object hashKey, Object value, Integer timeout);

    boolean hSet(String key, Map<Object, Object> map, Integer timeout);

    <T> T hGet(String key, Object hashKey);

    Map<Object, Object> hGetAll(String key);

    List<Object> hGetMultiple(String key, Object... hashKeys);

    long hDel(String key, Object... hashKeys);

    boolean hExists(String key, Object hashKey);

    Set<Object> hKeys(String key);

    /** 列表 数据类型接口
    -------------------------------------------
     */

    long lLeftPush(String key, Object value);

    long lLeftPush(String key, Object value, Integer timeout);

    long lLeftPush(String key, Object... values);

    long lLeftPush(String key, Integer timeout, Object... values);

    long lRightPush(String key, Object value);

    long lRightPush(String key, Object value, Integer timeout);

    long lRightPush(String key, Object... values);

    long lRightPush(String key, Integer timeout, Object... values);

    <T> T lLeftPop(String key);

    <T> T lRightPop(String key);

    <T> T lIndex(String key, long index);

    <T> List<T> lRange(String key, long start, long end);

    long lSize(String key);

    long lDel(String key, long count, Object value);

    boolean lTrim(String key, long start, long end);

    /** set 数据类型接口
     -------------------------------------------
     */

    boolean sAdd(String key, Object... values);

    boolean sAdd(String key, Integer timeout, Object... values);

    <T> Set<T> sMembers(String key);

    long sSize(String key);

    <T> Set<T> sDiff(String key, String... otherKeys);

    <T> Set<T> sIntersect(String key, String... otherKeys);

    <T> Set<T> sUnion(String key, String... otherKeys);

    long sDel(String key, Object... values);

    <T> T sPop(String key);

    boolean sIsMember(String key, Object object);

    /** sorted set 数据类型接口
     -------------------------------------------
     */

    long zAdd(String key, Set<ZSetOperations.TypedTuple<Object>> tuples);

    long zAdd(String key, Integer timeout, Set<ZSetOperations.TypedTuple<Object>> tuples);

    boolean zAdd(String key, Object value, double score);

    boolean zAdd(String key, Integer timeout, Object value, double score);

    long zCount(String key, double min, double max);

    long zDel(String key, Object... values);

    long zSize(String key);

    long zDelRangeByRank(String key, long start, long end);

    long zDelRangeByScore(String key, double min, double max);

    long zRank(String key, Object value);

    long zReverseRank(String key, Object value);

    Double zScore(String key, Object value);

    <T> Set<T> zRangeByRank(String key, long start, long end);

    <T> Set<T> zRangeByScore(String key, double min, double max);

    <T> Set<T> zRangeByScore(String key, double min, double max, long offset, long count);

    <T> Set<T> zRangeByScoreWithScores(String key, double min, double max, long offset, long count);

    <T> Set<T> zRangeByLex(String key, RedisZSetCommands.Range rang);

    <T> Set<T> zRangeByLex(String key, RedisZSetCommands.Range rang, RedisZSetCommands.Limit limit);

    <T> Set<T> zReverseRangeByRank(String key, long start, long end);

    <T> Set<T> zReverseRangeByScore(String key, double min, double max);

    <T> Set<T> zReverseRangeByScore(String key, double min, double max, long offset, long count);

    Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(String key, double min, double max);

    Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(String key, double min, double max, long offset, long count);
}
