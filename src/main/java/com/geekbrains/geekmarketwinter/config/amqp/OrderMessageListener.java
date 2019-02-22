package com.geekbrains.geekmarketwinter.config.amqp;

import com.geekbrains.geekmarketwinter.config.amqp.configuration.RabbitConfiguration;
import com.geekbrains.geekmarketwinter.config.support.ProducerOrder;
import com.geekbrains.geekmarketwinter.entites.Order;
import com.geekbrains.geekmarketwinter.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderMessageListener.class.getName());

    private final OrderService orderService;

    @Autowired
    public OrderMessageListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = RabbitConfiguration.QUEUE_ORDERS_IN)
    public void processOrder(ProducerOrder producerOrder) {
        logger.info("Order received: " + producerOrder);
        Order order = orderService.changeStatusAndSave(producerOrder);
        logger.info("Order updated: " + order);
    }

}
