package com.project.base.rabbitmq;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
@Configuration
public class RabbitMQBeanDefinitionRegistryPostProcessor implements ApplicationContextAware {

    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void createRabbitListenerContainerFactoryBeanDefinition(BeanDefinitionRegistry registry, String name) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SimpleRabbitListenerContainerFactory.class);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        registry.registerBeanDefinition(name, beanDefinition);
    }

    private void createRabbitAdminBeanDefinition(BeanDefinitionRegistry registry, String name) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RabbitAdmin.class);
        beanDefinitionBuilder.addConstructorArgValue(new CachingConnectionFactory());
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        registry.registerBeanDefinition(name, beanDefinition);
    }

    @Bean
    public BeanDefinitionRegistryPostProcessor factory(final Environment environment) {
        return new BeanDefinitionRegistryPostProcessor() {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

            }

            @Override
            public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
                int i = 0;
                while (true) {
                    String flag = environment.getProperty(MessageFormat.format("spring.rabbitmq.hosts[{0}].flag", i));
                    if (StringUtils.isBlank(flag))
                        break;
                    createRabbitListenerContainerFactoryBeanDefinition(registry, RabbitMQConfig.ContainerFactoryBeanPrefix + flag);
                    createRabbitAdminBeanDefinition(registry, RabbitMQConfig.AdminPrefix + flag);
                    i++;
                }
            }
        };
    }
}
