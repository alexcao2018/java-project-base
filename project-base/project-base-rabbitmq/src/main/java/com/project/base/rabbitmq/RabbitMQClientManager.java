package com.project.base.rabbitmq;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQClientManager {

    @Autowired
    @Qualifier("rabbitAdmin")
    private RabbitAdmin defaultRabbitAdmin;

    @Autowired
    private RabbitTemplate defaultRabbitTemplate;


    @Autowired
    private List<RabbitMQAdminWrapper> rabbitMQAdminWrapperCollection;

    @Autowired
    private List<RabbitMQTemplateWrapper> rabbitMQTemplateWrapperCollection;

    /**
     * 根据flag 选择 RabbitAdmin
     *
     * @param flag
     * @return
     */
    public  RabbitAdmin getRabbitAdminByFlag(String flag) {
        RabbitAdmin selectedRabbitAdmin = rabbitMQAdminWrapperCollection.stream().filter(x -> flag.equalsIgnoreCase(x.getFlag())).findFirst().get().getRabbitAdmin();
        return selectedRabbitAdmin;
    }

    /**
     * 根据flag 选择 RabbitTemplate
     *
     * @param flag
     * @return
     */
    public  RabbitTemplate getRabbitTemplateByFlag(String flag) {
        RabbitTemplate selectedRabbitTemplate = rabbitMQTemplateWrapperCollection.stream().filter(x -> flag.equalsIgnoreCase(x.getFlag())).findFirst().get().getRabbitTemplate();
        return selectedRabbitTemplate;
    }

    public RabbitTemplate getDefaultRabbitTemplate() {
        return defaultRabbitTemplate;
    }

    public RabbitAdmin getDefaultRabbitAdmin() {
        return defaultRabbitAdmin;
    }

}
