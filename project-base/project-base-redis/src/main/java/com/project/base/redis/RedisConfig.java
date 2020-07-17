package com.project.base.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties("spring.redis")
public class RedisConfig {

    private Pool pool;

    /* host config
    -----------------------------------------
     */

    /**
     * Database index used by the connection factory.
     */
    private int database = 0;

    /**
     * Redis url, which will overrule host, port and password if set.
     */
    private String url;

    /**
     * Redis server host.
     */
    private String host = "localhost";

    /**
     * Login password of the redis server.
     */
    private String password;

    /**
     * Redis server port.
     */
    private int port = 6379;

    /**
     * Enable SSL.
     */
    private boolean ssl;

    /**
     * Connection timeout in milliseconds.
     */
    private int timeout;

    private List<RedisHost> hosts;

    /**
     * 多个redis connection factory
     *
     * @return
     */
    @Bean
    public List<RedisConnectionFactoryWrapper> redisConnectionFactoryWrapperCollection() throws IllegalAccessException {

        List<RedisConnectionFactoryWrapper> redisConnectionFactoryWrapperCollection = new ArrayList<>();


        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMaxWaitMillis(pool.getMaxWait());

        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(-1);
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);



        if (hosts != null && hosts.size() > 0) {
            for (RedisHost redisHost : hosts) {
                JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);
                jedisConnectionFactory.setDatabase(redisHost.getDatabase());
                jedisConnectionFactory.setHostName(redisHost.getHost());
                jedisConnectionFactory.setPort(redisHost.getPort());
                jedisConnectionFactory.setTimeout(redisHost.getTimeout());
                if (StringUtils.isNotBlank(redisHost.getPassword()))
                    jedisConnectionFactory.setPassword(redisHost.getPassword());
                jedisConnectionFactory.setUseSsl(false);
                jedisConnectionFactory.afterPropertiesSet();

                RedisConnectionFactoryWrapper redisConnectionFactoryWrapper = new RedisConnectionFactoryWrapper();
                redisConnectionFactoryWrapper.setRedisConnectionFactory(jedisConnectionFactory);
                redisConnectionFactoryWrapper.setFlag(redisHost.getFlag());
                redisConnectionFactoryWrapperCollection.add(redisConnectionFactoryWrapper);
            }
        }

        return redisConnectionFactoryWrapperCollection;
    }

    /**
     * 多个redis template 模板对象
     *
     * @param redisConnectionFactoryWrapperCollection
     * @return
     */
    @Bean
    public List<RedisTemplateWrapper> redisTemplateWrapperCollection(List<RedisConnectionFactoryWrapper> redisConnectionFactoryWrapperCollection) {

        List<RedisTemplateWrapper> redisTemplateWrapperCollection = new ArrayList<>();

        for (RedisConnectionFactoryWrapper redisConnectionFactoryWrapper : redisConnectionFactoryWrapperCollection) {
            RedisTemplate<String, Object> template = createRedisTemplate(redisConnectionFactoryWrapper.getRedisConnectionFactory());

            /* 创建redisTemplateWrapper
            ---------------------------------------------
             */
            RedisTemplateWrapper redisTemplateWrapper = new RedisTemplateWrapper();
            redisTemplateWrapper.setRedisTemplate(template);
            redisTemplateWrapper.setRedisConnectionFactory(redisConnectionFactoryWrapper.getRedisConnectionFactory());
            redisTemplateWrapper.setFlag(redisConnectionFactoryWrapper.getFlag());
            redisTemplateWrapperCollection.add(redisTemplateWrapper);

        }
        return redisTemplateWrapperCollection;
    }

    /**
     * 多个redis client 对象
     *
     * @param redisTemplateWrapperCollection
     * @return
     */
    @Bean
    public List<RedisClientWrapper> redisClientWrapperCollection(List<RedisTemplateWrapper> redisTemplateWrapperCollection) throws IllegalAccessException {

        List<RedisClientWrapper> redisClientWrapperCollection = new ArrayList<>();
        for (RedisTemplateWrapper redisTemplateWrapper : redisTemplateWrapperCollection) {
            RedisClientImpl redisClient = new RedisClientImpl();
            redisClient.setRedisTemplate(redisTemplateWrapper.getRedisTemplate());

            redis.clients.util.Pool<Jedis> jedisPool = (redis.clients.util.Pool<Jedis>) FieldUtils.readField(redisTemplateWrapper.getRedisConnectionFactory(), "pool", true);
            redisClient.setJedisPool(jedisPool);

            RedisClientWrapper redisClientWrapper = new RedisClientWrapper();
            redisClientWrapper.setRedisClient(redisClient);
            redisClientWrapper.setFlag(redisTemplateWrapper.getFlag());

            redisClientWrapperCollection.add(redisClientWrapper);
        }

        return redisClientWrapperCollection;
    }


    /**
     * 默认redis template
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = createRedisTemplate(redisConnectionFactory);
        return template;
    }

    @Bean
    public redis.clients.util.Pool<Jedis> jedis(RedisConnectionFactory redisConnectionFactory) throws IllegalAccessException {
        redis.clients.util.Pool<Jedis> jedisPool = (redis.clients.util.Pool<Jedis>) FieldUtils.readField(redisConnectionFactory, "pool", true);
        return jedisPool;
    }

    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        //StringRedisTemplate的构造方法中默认设置了stringSerializer
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        //set flag serializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        //set value serializer
        template.setDefaultSerializer(jackson2JsonRedisSerializer);

        template.setConnectionFactory(redisConnectionFactory);
        template.afterPropertiesSet();

        return template;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public List<RedisHost> getHosts() {
        return hosts;
    }

    public void setHosts(List<RedisHost> hosts) {
        this.hosts = hosts;
    }

    public static class RedisHost {
        private String host;
        private int port;
        private String flag;
        private String password;
        private int database;
        private int timeout;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getDatabase() {
            return database;
        }

        public void setDatabase(int database) {
            this.database = database;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }

    public static class Pool {

        /**
         * Max number of "idle" connections in the pool. Use a negative value to indicate
         * an unlimited number of idle connections.
         */
        private int maxIdle = 8;

        /**
         * Target for the minimum number of idle connections to maintain in the pool. This
         * setting only has an effect if it is positive.
         */
        private int minIdle = 0;

        /**
         * Max number of connections that can be allocated by the pool at a given time.
         * Use a negative value for no limit.
         */
        private int maxActive = 8;

        /**
         * Maximum amount of time (in milliseconds) a connection allocation should block
         * before throwing an exception when the pool is exhausted. Use a negative value
         * to block indefinitely.
         */
        private int maxWait = -1;

        public int getMaxIdle() {
            return this.maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMinIdle() {
            return this.minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getMaxActive() {
            return this.maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        public int getMaxWait() {
            return this.maxWait;
        }

        public void setMaxWait(int maxWait) {
            this.maxWait = maxWait;
        }

    }

}
