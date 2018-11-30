package com.project.base.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisClientManager {

    @Autowired
    private RedisClient defaultRedisClient;

    @Autowired
    private List<RedisClientWrapper> redisClientWrapperCollection;

    /**
     * 根据flag 选择 redis client
     *
     * @param flag
     * @return
     */
    public  RedisClient getRedisClientByFlag(String flag) {
        RedisClient selectedRedisClient = redisClientWrapperCollection.stream().filter(x -> flag.equalsIgnoreCase(x.getFlag())).findFirst().get().getRedisClient();
        return selectedRedisClient;
    }

    /**
     * 获取默认redis client , spring boot 自动注入的redis template
     * @return
     */
    public RedisClient getDefaultRedisClient() {
        return defaultRedisClient;
    }

}
