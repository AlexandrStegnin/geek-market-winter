package ru.stegnin.orderconsumer.amqp.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@EnableRabbit
@Configuration
public class RabbitConfiguration implements RabbitListenerConfigurer {

    public static final String QUEUE_ORDERS_OUT = "queue-orders-out";
    public static final String QUEUE_ORDERS_IN = "queue-orders-in";
    public static final String EXCHANGE_ORDERS = "exchange-orders";

    @Bean
    public Queue ordersQueueOut() {
        return QueueBuilder.durable(QUEUE_ORDERS_OUT).build();
    }

    @Bean
    public Queue ordersQueueIn() {
        return QueueBuilder.durable(QUEUE_ORDERS_IN).build();
    }

    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(EXCHANGE_ORDERS, true, false);
    }

    @Bean
    public Binding bindingOut() {
        return BindingBuilder.bind(ordersQueueOut()).to(ordersExchange()).with(QUEUE_ORDERS_OUT);
    }

    @Bean
    public Binding bindingIn() {
        return BindingBuilder.bind(ordersQueueIn()).to(ordersExchange()).with(QUEUE_ORDERS_IN);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        messageConverter.setJavaTypeMapper(new DefaultJackson2JavaTypeMapper());
        return messageConverter;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        rabbitListenerEndpointRegistrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    @Bean
    public MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory =
                new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setMessageConverter(consumerJackson2MessageConverter());
        return messageHandlerMethodFactory;
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

}
