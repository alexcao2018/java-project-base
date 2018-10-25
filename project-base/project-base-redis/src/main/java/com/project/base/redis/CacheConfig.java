package com.project.base.redis;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Method;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        // 设置默认过期时间为60秒
        cacheManager.setDefaultExpiration(60);
        cacheManager.setUsePrefix(true);
        return cacheManager;
    }

    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... args) {
                StringBuilder sb = new StringBuilder();
                sb.append(o.getClass().getName()).append("#");
                sb.append(method.getName()).append("(");
                for (Object obj : args) {
                    sb.append(obj.toString()).append(",");
                }
                sb.append(")");
                return sb.toString();
            }
        };
    }



}
