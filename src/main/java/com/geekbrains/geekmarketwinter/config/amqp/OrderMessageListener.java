package com.geekbrains.geekmarketwinter.config.amqp;

import com.geekbrains.geekmarketwinter.config.amqp.configuration.RabbitConfiguration;
import com.geekbrains.geekmarketwinter.entites.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderMessageListener.class.getName());

    @RabbitListener(queues = RabbitConfiguration.QUEUE_ORDERS_IN)
    public void processOrder(Order order) {
        logger.info("Order received: " + order);
    }

}
