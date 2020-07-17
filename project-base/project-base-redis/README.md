说明：
目前只支持spring boot 接入
接入方式：

1、启动类

    @SpringBootApplication
    @ComponentScan({"com.project.base.redis"})
    public class App {
        public static void main(String[] args) {
            SpringApplication.run(App.class, args);
        }
    }
    
2、yml 文件

    spring:
      redis:
        hosts:
          - host: 192.168.8.10
            port: 6379
            flag: sz
          - host: 192.168.8.10
            port: 6379
            flag: sh
          - host: 192.168.8.10
            port: 6379
            flag: wx
        pool:
          max-active: 16
          max-wait: 5000
          
        host: 192.168.8.10
    
3、方法上加标记

    @GetMapping("/index")
    @RedisCacheable(cacheName = "test",key = "#city", flagExpression = "#city")
    public String index(@PathVariable("city") String city) {
        return "ok";
    }


    @GetMapping("/evict")
    @RedisCacheEvict(cacheName = "test",key = "#city", flagExpression = "#city")
    public String evict(@PathVariable("city") String city) {
        return "ok";
    }

4、 由于redisTemplate 序列化对象，默认使用了Jackson2JsonRedisSerializer 序列化，
    Jackson2JsonRedisSerializer 将类型也放入json中。如果需要存储纯粹的json,
    
    1、使用jedis原生客户端

    用法：
       Jedis jedis = redisClient.getJedisPool().getResource();
       jedis.set("key", JsonTool.serialize(datasource));
       jedis.get("key");
       jedis.close();
    
    2、设置ValueSerializer
    
      defaultRedisClient.getRedisTemplate().setValueSerializer(new StringRedisSerializer());
      defaultRedisClient.set("api-activity-live-coupon-userCouponInfo:" + request.getCustomerGuid(), JsonTool.serialize(commonResponse), 60 * 60 * 4);
    