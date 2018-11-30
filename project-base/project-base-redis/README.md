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

    
一、redis 事务

Redis通过multi, exec, 或discard命令来提供事务支持，这些操作在RedisTemplate中同样是可用的。
但是，RedisTemplate 默认使用RedisCallBack接口，并不能保证使用同一连接来执行同一事务中的所有操作（此时Transaction是无效的）。

又但是，Spring Data Redis提供了SessionCallback接口，以便在需要保证同一连接执行多个操作时使用，
比如“需要使用Redis事务时”。 

使用方式：

@Autowired
private RedisTemplate<String, Object> redisTemplate;

//execute a transaction
List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
  public List<Object> execute(RedisOperations operations) throws DataAccessException {
    operations.multi();
    operations.opsForSet().add("flag", "value1");

    // This will contain the results of all ops in the transaction
    return operations.exec();
  }
});