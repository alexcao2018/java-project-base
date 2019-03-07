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

4、 由于redisTemplate 序列化对象，会将类型也放入json中，可以使用jedis原生客户端

    用法：
       Jedis jedis = redisClient.getJedisPool().getResource();
       jedis.set("key", JsonTool.serialize(datasource));
       jedis.get("key");
       