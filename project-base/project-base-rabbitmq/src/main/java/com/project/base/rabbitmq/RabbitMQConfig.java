package com.project.base.rabbitmq;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties("spring.rabbitmq")
@EnableConfigurationProperties(RabbitMQProperties.class)
public class RabbitMQConfig {

    public static final String ContainerFactoryBeanPrefix = "rabbitmq-containerFactory-";
    public static final String AdminPrefix = "rabbitmq-admin-";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    private List<RabbitMQTemplateWrapper> rabbitMQTemplateWrapperCollection = new ArrayList<>();

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        return rabbitAdmin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }


    /**
     * rabbitMQ connection factory 集合
     *
     * @return
     */
    @Bean
    public List<RabbitMQConnectionFactoryWrapper> rabbitMQConnectionFactoryWrapperCollection() throws Exception {

        List<RabbitMQConnectionFactoryWrapper> rabbitMQConnectionFactoryWrapperCollection = new ArrayList<>();

        List<RabbitMQProperties.RabbitMQHost> hosts = rabbitMQProperties.getHosts();
        if (hosts != null && hosts.size() > 0) {
            for (RabbitMQProperties.RabbitMQHost rabbitMQHost : hosts) {
                CachingConnectionFactory factory = new CachingConnectionFactory();
                factory.setHost(rabbitMQHost.getHost());
                factory.setUsername(rabbitMQHost.getUsername());
                factory.setPassword(rabbitMQHost.getPassword());
                factory.setPort(rabbitMQHost.getPort());
                factory.setVirtualHost(rabbitMQHost.getVirtualHost());
                factory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
                factory.afterPropertiesSet();

                RabbitMQConnectionFactoryWrapper rabbitMQConnectionFactoryWrapper = new RabbitMQConnectionFactoryWrapper();
                rabbitMQConnectionFactoryWrapper.setConnectionFactory(factory);
                rabbitMQConnectionFactoryWrapper.setFlag(rabbitMQHost.getFlag());

                rabbitMQConnectionFactoryWrapperCollection.add(rabbitMQConnectionFactoryWrapper);
            }
        }

        return rabbitMQConnectionFactoryWrapperCollection;
    }

    /**
     * rabbitmq listener container factory 集合
     *
     * @param rabbitMQConnectionFactoryWrapperCollection
     * @return
     */
    @Bean
    public List<RabbitListenerContainerFactoryWrapper> rabbitListenerContainerFactoryCollection(List<RabbitMQConnectionFactoryWrapper> rabbitMQConnectionFactoryWrapperCollection) {
        List<RabbitListenerContainerFactoryWrapper> rabbitListenerContainerFactoryWrapperCollection = new ArrayList<>();
        for (RabbitMQConnectionFactoryWrapper rabbitMQConnectionFactoryWrapper : rabbitMQConnectionFactoryWrapperCollection) {

            /* 获取在RabbitMQBeanDefinitionRegistryPostProcessor 中注册的bean,进行属性更改
            ----------------------------------------------------
             */
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = defaultListableBeanFactory.getBean(ContainerFactoryBeanPrefix + rabbitMQConnectionFactoryWrapper.getFlag(), SimpleRabbitListenerContainerFactory.class);

            simpleRabbitListenerContainerFactory.setConnectionFactory(rabbitMQConnectionFactoryWrapper.getConnectionFactory());
            simpleRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());

            RabbitListenerContainerFactoryWrapper rabbitListenerContainerFactoryWrapper = new RabbitListenerContainerFactoryWrapper();
            rabbitListenerContainerFactoryWrapper.setRabbitListenerContainerFactory(simpleRabbitListenerContainerFactory);
            rabbitListenerContainerFactoryWrapper.setFlag(rabbitMQConnectionFactoryWrapper.getFlag());

            rabbitListenerContainerFactoryWrapperCollection.add(rabbitListenerContainerFactoryWrapper);
        }

        return rabbitListenerContainerFactoryWrapperCollection;
    }


    /**
     * rabbitmq admin 集合
     *
     * @param rabbitMQConnectionFactoryWrapperCollection
     * @return
     */
    @Bean
    public List<RabbitMQAdaminWrapper> rabbitAdminCollection(List<RabbitMQConnectionFactoryWrapper> rabbitMQConnectionFactoryWrapperCollection) throws Exception {

        List<RabbitMQAdaminWrapper> rabbitMQAdminWrapperCollection = new ArrayList<>();
        List<RabbitMQTemplateWrapper> rabbitMQTemplateWrapperCollection = initRabbitTemplate(rabbitMQConnectionFactoryWrapperCollection);
        for (RabbitMQConnectionFactoryWrapper rabbitMQConnectionFactoryWrapper : rabbitMQConnectionFactoryWrapperCollection) {

            /* 获取在RabbitMQBeanDefinitionRegistryPostProcessor 中注册的bean,进行属性更改
            ----------------------------------------------------*/
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

            RabbitAdmin rabbitAdmin = defaultListableBeanFactory.getBean(AdminPrefix + rabbitMQConnectionFactoryWrapper.getFlag(), RabbitAdmin.class);
            setFinalStatic(rabbitAdmin, RabbitAdmin.class.getDeclaredField("connectionFactory"), rabbitMQConnectionFactoryWrapper.getConnectionFactory());

            RabbitMQTemplateWrapper rabbitMQTemplateWrapper = rabbitMQTemplateWrapperCollection.stream().filter(x -> x.getFlag().equalsIgnoreCase(rabbitMQConnectionFactoryWrapper.getFlag())).findAny().get();
            setFinalStatic(rabbitAdmin, RabbitAdmin.class.getDeclaredField("rabbitTemplate"), rabbitMQTemplateWrapper.getRabbitTemplate());

            RabbitMQAdaminWrapper rabbitMQAdaminWrapper = new RabbitMQAdaminWrapper();
            rabbitMQAdaminWrapper.setRabbitAdmin(rabbitAdmin);
            rabbitMQAdaminWrapper.setFlag(rabbitMQConnectionFactoryWrapper.getFlag());

            rabbitMQAdminWrapperCollection.add(rabbitMQAdaminWrapper);
        }
        return rabbitMQAdminWrapperCollection;
    }

    /**
     * rabbitmq template 集合
     *
     * @param rabbitMQConnectionFactoryWrapperCollection
     * @return
     */
    @Bean
    public List<RabbitMQTemplateWrapper> rabbitTemplateCollection(List<RabbitMQConnectionFactoryWrapper> rabbitMQConnectionFactoryWrapperCollection) {
        return initRabbitTemplate(rabbitMQConnectionFactoryWrapperCollection);
    }

    private List<RabbitMQTemplateWrapper> initRabbitTemplate(List<RabbitMQConnectionFactoryWrapper> rabbitMQConnectionFactoryWrapperCollection) {
        if (rabbitMQTemplateWrapperCollection.size() > 0)
            return rabbitMQTemplateWrapperCollection;
        for (RabbitMQConnectionFactoryWrapper rabbitMQConnectionFactoryWrapper : rabbitMQConnectionFactoryWrapperCollection) {

            RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitMQConnectionFactoryWrapper.getConnectionFactory());
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

            RabbitMQTemplateWrapper rabbitMQTemplateWrapper = new RabbitMQTemplateWrapper();
            rabbitMQTemplateWrapper.setRabbitTemplate(rabbitTemplate);
            rabbitMQTemplateWrapper.setFlag(rabbitMQConnectionFactoryWrapper.getFlag());

            rabbitMQTemplateWrapperCollection.add(rabbitMQTemplateWrapper);
        }
        return rabbitMQTemplateWrapperCollection;
    }

    void setFinalStatic(Object object, Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(object, newValue);
    }


}
