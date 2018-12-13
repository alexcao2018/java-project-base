package com.project.base.rabbitmq;

import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;

public class RabbitListenerContainerFactoryWrapper {
    private RabbitListenerContainerFactory rabbitListenerContainerFactory;
    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public RabbitListenerContainerFactory getRabbitListenerContainerFactory() {
        return rabbitListenerContainerFactory;
    }

    public void setRabbitListenerContainerFactory(RabbitListenerContainerFactory rabbitListenerContainerFactory) {
        this.rabbitListenerContainerFactory = rabbitListenerContainerFactory;
    }
}
