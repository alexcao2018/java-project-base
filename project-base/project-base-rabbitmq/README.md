说明：
1、引入pom
    <dependency>
        <groupId>com.project.base</groupId>
        <artifactId>project-base-rabbitmq</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    
2、启动类上加入注解
  @ComponentScan({"com.project.base.rabbitmq"})
  
3、配置文件

    spring:
      rabbitmq:
        host: 192.168.8.11
        username: root
        password: 1
        port: 5672
        virtual-host: /
        hosts:
          - host: 192.168.8.11
            username: root
            password: 1
            port: 5672
            flag: test1
          - host: 192.168.8.12
            username: root
            password: 1
            port: 5672
            flag: test2
            
4、代码示例

     @RabbitListener(bindings ={@QueueBinding(value = @Queue(value = "q66",durable = "true"),
                exchange =@Exchange(value = "q66.exchange",durable = "true"),key = "mytestkey")})
        public void handleMessage(@Payload QueueObject body, @Headers Map<String,Object> headers){
            System.out.println("====消费消息===handleMessage");
            System.out.println(headers);
            System.out.println(body);
        }
    以下都在注解中修改
    1）修改containerFactory
        rabbitmq-containerFactory-{flag}
    2) 修改rabbit admin
        rabbitmq-admin-{flag}