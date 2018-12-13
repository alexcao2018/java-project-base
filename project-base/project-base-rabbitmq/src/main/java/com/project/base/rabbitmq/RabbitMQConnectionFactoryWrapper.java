package com.project.base.rabbitmq;


import org.springframework.amqp.rabbit.connection.ConnectionFactory;

public class RabbitMQConnectionFactoryWrapper {
    private ConnectionFactory connectionFactory;
    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
