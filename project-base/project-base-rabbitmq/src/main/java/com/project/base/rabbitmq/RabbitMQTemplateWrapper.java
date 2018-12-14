package com.project.base.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitMQTemplateWrapper {
    private RabbitTemplate rabbitTemplate;
    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public RabbitTemplate getRabbitTemplate() {
        return rabbitTemplate;
    }

    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
}
