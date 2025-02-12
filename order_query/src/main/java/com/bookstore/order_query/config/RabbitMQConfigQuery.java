package com.bookstore.order_query.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfigQuery {

    public static final String ORDER_EXCHANGE = "order.exchange";

    public static final String ORDER_CREATED_RK = "order.created";
    public static final String ORDER_STATUS_UPDATE_RK = "order.status.update";

    // Colas exclusivas
    public static final String ORDER_CREATED_QUEUE_QUERY = "order.created.queue.query";
    public static final String ORDER_STATUS_UPDATE_QUEUE_QUERY = "order.status.update.queue.query";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderCreatedQueueQuery() {
        return new Queue(ORDER_CREATED_QUEUE_QUERY, true);
    }

    @Bean
    public Queue orderStatusUpdateQueueQuery() {
        return new Queue(ORDER_STATUS_UPDATE_QUEUE_QUERY, true);
    }

    @Bean
    public Binding orderCreatedBindingQuery(Queue orderCreatedQueueQuery, DirectExchange orderExchange) {
        return BindingBuilder
                .bind(orderCreatedQueueQuery)
                .to(orderExchange)
                .with(ORDER_CREATED_RK);
    }

    @Bean
    public Binding orderStatusUpdateBindingQuery(Queue orderStatusUpdateQueueQuery, DirectExchange orderExchange) {
        return BindingBuilder
                .bind(orderStatusUpdateQueueQuery)
                .to(orderExchange)
                .with(ORDER_STATUS_UPDATE_RK);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}

