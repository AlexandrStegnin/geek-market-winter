package ru.stegnin.orderconsumer.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.stegnin.orderconsumer.amqp.configuration.RabbitConfiguration;
import ru.stegnin.orderconsumer.entities.OrderStatus;
import ru.stegnin.orderconsumer.entities.ProducerOrder;

@Component
public class OrderMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderMessageListener.class.getName());

    private final OrderMessageSender messageSender;

    @Autowired
    public OrderMessageListener(OrderMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @RabbitListener(queues = RabbitConfiguration.QUEUE_ORDERS_OUT)
    public void processOrder(ProducerOrder order) throws InterruptedException {
        logger.info("Order received: " + order);
        Thread.sleep(10000);
        order.setStatus(new OrderStatus());
        messageSender.sendOrder(order);
    }

}
