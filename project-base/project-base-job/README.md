说明：
目前只支持spring boot 接入
接入方式：

1、启动类

    @SpringBootApplication
    @ComponentScan({"com.project.base.job"})
    public class App {
        public static void main(String[] args) {
            SpringApplication.run(App.class, args);
        }
    }
    
2、yml 文件 加入

    job:
      poolSize: 15
    