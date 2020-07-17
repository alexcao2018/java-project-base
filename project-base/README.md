说明：jackson String to Date

## 1. 设置日期格式
```
@Bean
        public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
            MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
            //设置日期格式
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            objectMapper.setDateFormat(smt);
            mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
            //设置中文编码格式
            List<MediaType> list = new ArrayList<MediaType>();
            list.add(MediaType.APPLICATION_JSON_UTF8);
            mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
            return mappingJackson2HttpMessageConverter;
         }
```
 存在问题：
    1、 不能支持 yyyy-MM-dd
    2、不能支持多个格式。
 临时解决：
    在类字段上加
    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss") 自定义控制格式
    
 ## 2. 配置中心 修改 logger 级别
 
 1. 进入apollo 配置中心
 2. 增加 "logger.level." 的配置项，例如：logger.level.com.project.base.web.config
 
  