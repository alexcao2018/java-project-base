package com.project.base.redis;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.project.base.redis.annotation.LocalCacheEvict;
import com.project.base.redis.annotation.LocalCacheable;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class LocalCacheableAspect {

    @Autowired
    private CacheHelper cacheHelper;

    private Map<String, LoadingCache<String, Object>> loadingCacheMap = new HashMap<>();

    private Map<String, Caffeine<Object, Object>> supportMap = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(LocalCacheableAspect.class);

    @Pointcut("@annotation(com.project.base.redis.annotation.LocalCacheable)")
    public void pointcutCacheableMethod() {

    }

    @Pointcut("@annotation(com.project.base.redis.annotation.LocalCacheEvict)")
    public void pointcutCacheEvictMethod() {

    }

    @Around("pointcutCacheableMethod()")
    public Object aroundLocalCache(ProceedingJoinPoint joinPoint) {
        Object result = null;
        Method method = cacheHelper.getMethod(joinPoint);
        LocalCacheable redisCacheable = method.getAnnotation(LocalCacheable.class);
        String cacheKey = getCacheKey(redisCacheable, joinPoint, method);

        LoadingCache<String, Object> loadingCache = loadingCacheMap.get(cacheKey);
        if (loadingCache != null) {
            Object obj = loadingCache.getIfPresent(cacheKey);
            if (obj != null) {
                return obj;
            }

        } else {
            Caffeine<Object, Object> supply = Caffeine.newBuilder()
                    .expireAfterWrite(redisCacheable.localCacheTime(), TimeUnit.SECONDS)
                    .maximumSize(10000)
                    .refreshAfterWrite(redisCacheable.localCacheRefreshTime(), TimeUnit.SECONDS);
            if (supportMap.putIfAbsent(cacheKey, supply) == null) {
                loadingCache = supply.build((x) -> method.invoke(joinPoint.getTarget(), joinPoint.getArgs()));
                loadingCacheMap.put(cacheKey, loadingCache);
            }
        }
        try {

            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
        }
        if (loadingCache != null) {
            loadingCache.put(cacheKey, result);
        }
        return result;
    }

    @Before("pointcutCacheEvictMethod()")
    public void beforeLocalCacheEvict(JoinPoint joinPoint) {
        Method method = cacheHelper.getMethod(joinPoint);
        LocalCacheEvict redisCacheable = method.getAnnotation(LocalCacheEvict.class);
        String cacheKey = getCacheKey(redisCacheable, joinPoint, method);
        if (StringUtils.isEmpty(cacheKey))
            return;
        loadingCacheMap.remove(method.getName());
    }

    private String getCacheKey(LocalCacheable localCacheable, JoinPoint joinPoint, Method method) {
        String cachePrefix = localCacheable.cacheName();
        String cacheSuffix = cacheHelper.getSuffixByGenerator(joinPoint, localCacheable.key(), localCacheable.keyGenerator(), method);
        String splitter = localCacheable.splitter();
        String cacheKey = MessageFormat.format("{0}{1}{2}", cachePrefix, splitter, cacheSuffix);
        return cacheKey;
    }

    private String getCacheKey(LocalCacheEvict localCacheable, JoinPoint joinPoint, Method method) {
        String cachePrefix = localCacheable.cacheName();
        String cacheSuffix = cacheHelper.getSuffixByGenerator(joinPoint, localCacheable.key(), localCacheable.keyGenerator(), method);
        String splitter = localCacheable.splitter();
        String cacheKey = MessageFormat.format("{0}{1}{2}", cachePrefix, splitter, cacheSuffix);
        return cacheKey;
    }
}
