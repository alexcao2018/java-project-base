package com.project.base.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitAdmin;

public class RabbitMQAdminWrapper {
    private RabbitAdmin rabbitAdmin;
    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public RabbitAdmin getRabbitAdmin() {
        return rabbitAdmin;
    }

    public void setRabbitAdmin(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }
}
