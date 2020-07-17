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
        listener:
          concurrency: 3  # 并行处理线程数
          max-concurrency: 10 # 最大并行处理线程数
          prefetch: 2 # 提前获取消息数
        cache:
          channel:
            size: 10
          connection:
            mode: connection  # 如果 model 为 channel , size 值不能设置，否则报错
            size: 5
        
            
4、代码示例

     @RabbitListener(queues = "queue.recharge.service.mall.identity.number"
                 ,containerFactory = "rabbitmq-containerFactory-test")
         public void handleMessage(Message message) throws InterruptedException {
             System.out.println(Thread.currentThread().getId());
             Thread.sleep(1000*10);
             System.out.println("====消费消息===handleMessage");
         }
    以下都在注解中修改
    1）修改containerFactory
        rabbitmq-containerFactory-{flag}
    2) 修改rabbit admin
        rabbitmq-admin-{flag}