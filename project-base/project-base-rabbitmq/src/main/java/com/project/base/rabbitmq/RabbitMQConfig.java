package com.project.base.rabbitmq;

import com.project.base.common.lang.reflect.ReflectTool;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties("spring.rabbitmq")
@EnableConfigurationProperties(RabbitMQProperties.class)
public class RabbitMQConfig implements RabbitListenerConfigurer {

    public static final String ContainerFactoryBeanPrefix = "rabbitmq-containerFactory-";
    public static final String AdminPrefix = "rabbitmq-admin-";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    private List<RabbitMQTemplateWrapper> rabbitMQTemplateWrapperCollection = new ArrayList<>();

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        if (connectionFactory instanceof CachingConnectionFactory) {
            CachingConnectionFactory cachingConnectionFactory = (CachingConnectionFactory) connectionFactory;
            cachingConnectionFactory.getRabbitConnectionFactory().setHandshakeTimeout(60 * 1000);
        }
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

        RabbitMQProperties.Cache cacheConfig = rabbitMQProperties.getCache();
        if (hosts != null && hosts.size() > 0) {
            for (RabbitMQProperties.RabbitMQHost rabbitMQHost : hosts) {
                CachingConnectionFactory factory = new CachingConnectionFactory();
                factory.setHost(rabbitMQHost.getHost());
                factory.setUsername(rabbitMQHost.getUsername());
                factory.setPassword(rabbitMQHost.getPassword());
                factory.setPort(rabbitMQHost.getPort());
                factory.setVirtualHost(rabbitMQHost.getVirtualHost());
                factory.getRabbitConnectionFactory().setHandshakeTimeout(1000 * 60);

                if (cacheConfig.getChannel().getSize() != null) {
                    factory.setChannelCacheSize(cacheConfig.getChannel().getSize());
                }

                if (cacheConfig.getChannel().getCheckoutTimeout() != null) {
                    factory.setChannelCheckoutTimeout(cacheConfig.getChannel().getCheckoutTimeout());
                }

                if (cacheConfig.getConnection().getMode() != null) {
                    factory.setCacheMode(cacheConfig.getConnection().getMode());
                }

                if (cacheConfig.getConnection().getSize() != null) {
                    factory.setConnectionCacheSize(cacheConfig.getConnection().getSize());
                }

                factory.afterPropertiesSet();

                RabbitMQConnectionFactoryWrapper rabbitMQConnectionFactoryWrapper = new RabbitMQConnectionFactoryWrapper();
                rabbitMQConnectionFactoryWrapper.setConnectionFactory(factory);
                rabbitMQConnectionFactoryWrapper.setFlag(rabbitMQHost.getFlag());
                rabbitMQConnectionFactoryWrapper.setRabbitMQHostProperty(rabbitMQHost);

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
            if (!rabbitMQConnectionFactoryWrapper.getRabbitMQHostProperty().getDisableListenerConverter()) {
                simpleRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
            }

            RabbitMQProperties.Listener listenerConfig = rabbitMQProperties.getListener();
            simpleRabbitListenerContainerFactory.setAutoStartup(listenerConfig.isAutoStartup());
            if (listenerConfig.getAcknowledgeMode() != null) {
                simpleRabbitListenerContainerFactory.setAcknowledgeMode(listenerConfig.getAcknowledgeMode());
            }
            if (listenerConfig.getConcurrency() != null) {
                simpleRabbitListenerContainerFactory.setConcurrentConsumers(listenerConfig.getConcurrency());
            }
            if (listenerConfig.getMaxConcurrency() != null) {
                simpleRabbitListenerContainerFactory.setMaxConcurrentConsumers(listenerConfig.getMaxConcurrency());
            }
            if (listenerConfig.getPrefetch() != null) {
                simpleRabbitListenerContainerFactory.setPrefetchCount(listenerConfig.getPrefetch());
            }
            if (listenerConfig.getTransactionSize() != null) {
                simpleRabbitListenerContainerFactory.setTxSize(listenerConfig.getTransactionSize());
            }
            if (listenerConfig.getDefaultRequeueRejected() != null) {
                simpleRabbitListenerContainerFactory.setDefaultRequeueRejected(listenerConfig.getDefaultRequeueRejected());
            }
            if (listenerConfig.getIdleEventInterval() != null) {
                simpleRabbitListenerContainerFactory.setIdleEventInterval(listenerConfig.getIdleEventInterval());
            }

           /* RabbitProperties.ListenerRetry retryConfig = listenerConfig.getRetry();
            if (retryConfig.isEnabled()) {
                RetryInterceptorBuilder<?> builder = (retryConfig.isStateless()
                        ? RetryInterceptorBuilder.stateless()
                        : RetryInterceptorBuilder.stateful());
                builder.maxAttempts(retryConfig.getMaxAttempts());
                builder.backOffOptions(retryConfig.getInitialInterval(),
                        retryConfig.getMultiplier(), retryConfig.getMaxInterval());
                MessageRecoverer recoverer = (this.messageRecoverer != null
                        ? this.messageRecoverer : new RejectAndDontRequeueRecoverer());
                builder.recoverer(recoverer);
                factory.setAdviceChain(builder.build());
            }*/


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
    public List<RabbitMQAdminWrapper> rabbitAdminCollection(List<RabbitMQConnectionFactoryWrapper> rabbitMQConnectionFactoryWrapperCollection) throws Exception {

        List<RabbitMQAdminWrapper> rabbitMQAdminWrapperCollection = new ArrayList<>();
        List<RabbitMQTemplateWrapper> rabbitMQTemplateWrapperCollection = initRabbitTemplate(rabbitMQConnectionFactoryWrapperCollection);
        for (RabbitMQConnectionFactoryWrapper rabbitMQConnectionFactoryWrapper : rabbitMQConnectionFactoryWrapperCollection) {

            /* 获取在RabbitMQBeanDefinitionRegistryPostProcessor 中注册的bean,进行属性更改
            ----------------------------------------------------*/
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

            RabbitAdmin rabbitAdmin = defaultListableBeanFactory.getBean(AdminPrefix + rabbitMQConnectionFactoryWrapper.getFlag(), RabbitAdmin.class);
            ReflectTool.setFinalFeildValue(rabbitAdmin, RabbitAdmin.class.getDeclaredField("connectionFactory"), rabbitMQConnectionFactoryWrapper.getConnectionFactory());

            RabbitMQTemplateWrapper rabbitMQTemplateWrapper = rabbitMQTemplateWrapperCollection.stream().filter(x -> x.getFlag().equalsIgnoreCase(rabbitMQConnectionFactoryWrapper.getFlag())).findAny().get();
            ReflectTool.setFinalFeildValue(rabbitAdmin, RabbitAdmin.class.getDeclaredField("rabbitTemplate"), rabbitMQTemplateWrapper.getRabbitTemplate());

            RabbitMQAdminWrapper rabbitMQAdminWrapper = new RabbitMQAdminWrapper();
            rabbitMQAdminWrapper.setRabbitAdmin(rabbitAdmin);
            rabbitMQAdminWrapper.setFlag(rabbitMQConnectionFactoryWrapper.getFlag());

            rabbitMQAdminWrapperCollection.add(rabbitMQAdminWrapper);
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

    @Bean
    public DefaultMessageHandlerMethodFactory defaultMessageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(new MappingJackson2MessageConverter());
        return factory;
    }


    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        if (!rabbitMQProperties.getDisableListenerConverter()) {
            registrar.setMessageHandlerMethodFactory(defaultMessageHandlerMethodFactory());
        }
    }
}
