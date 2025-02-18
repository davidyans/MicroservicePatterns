package com.bookstore.microservice.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";

    public static final String ORDER_CREATED_RK = "order.created";
    public static final String ORDER_STATUS_UPDATE_RK = "order.status.update";

    public static final String ORDER_STATUS_UPDATE_QUEUE = "order.status.update.queue";

    public static final String ORDER_CREATED_QUEUE_FROM_COMMAND = "order.created.queue.from.command";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderStatusUpdateQueue() {
        return new Queue(ORDER_STATUS_UPDATE_QUEUE, true);
    }


    @Bean
    public Queue orderCreatedQueueFromCommand() {
        return new Queue(ORDER_CREATED_QUEUE_FROM_COMMAND, true);
    }

    @Bean
    public Binding orderStatusUpdateBinding(Queue orderStatusUpdateQueue, DirectExchange orderExchange) {
        return BindingBuilder
                .bind(orderStatusUpdateQueue)
                .to(orderExchange)
                .with(ORDER_STATUS_UPDATE_RK);
    }

    @Bean
    public Binding orderCreatedQueueFromCommandBinding(
            @Qualifier("orderCreatedQueueFromCommand") Queue queue,
            DirectExchange exchange
    ) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ORDER_CREATED_RK);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
