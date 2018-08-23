说明：
1、mybatis 扩展项目
2、支持通用mapper
3、支持动态criteria查询
4、支持映射
5、集成PageHelper , 支持分页

目前只支持spring boot 接入
接入方式：
    @SpringBootApplication
    @ComponentScan({"com.project.base.mybatis"})
    public class App {
        public static void main(String[] args) {
            SpringApplication.run(App.class, args);
        }
    }