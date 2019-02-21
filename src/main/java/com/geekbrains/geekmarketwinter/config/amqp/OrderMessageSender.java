package com.geekbrains.geekmarketwinter.config.amqp;

import com.geekbrains.geekmarketwinter.config.amqp.configuration.RabbitConfiguration;
import com.geekbrains.geekmarketwinter.entites.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderMessageSender {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrder(Order order) {
        this.rabbitTemplate.convertAndSend(RabbitConfiguration.QUEUE_ORDERS_OUT, order);
    }

}
