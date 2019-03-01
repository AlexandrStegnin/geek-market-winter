package ru.stegnin.orderconsumer.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.stegnin.orderconsumer.amqp.configuration.RabbitConfiguration;
import ru.stegnin.orderconsumer.entities.ProducerOrder;

@Service
public class OrderMessageSender {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrder(ProducerOrder order) {
        this.rabbitTemplate.convertAndSend(RabbitConfiguration.QUEUE_ORDERS_IN, order);
    }

}
