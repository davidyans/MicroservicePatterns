package com.bookstore.order_command.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class RabbitMQConfigCommand {

    public static final String ORDER_EXCHANGE = "order.exchange";

    // Routing keys
    public static final String ORDER_CREATED_RK = "order.created";
    public static final String ORDER_STATUS_UPDATE_RK = "order.status.update";

    // Cola para escuchar eventos de creación de órdenes del MS original
    public static final String ORDER_CREATED_QUEUE_COMMAND = "order.created.queue.command";

    // Cola para escuchar eventos de actualización de estado del MS original
    public static final String ORDER_STATUS_UPDATE_QUEUE_COMMAND = "order.status.update.queue.command";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    // Cola donde ESTE nuevo MS escuchará los eventos "order.created" del MS original
    @Bean
    public Queue orderCreatedQueueCommand() {
        return new Queue(ORDER_CREATED_QUEUE_COMMAND, true);
    }

    @Bean
    public Binding orderCreatedQueueCommandBinding(
            Queue orderCreatedQueueCommand,
            DirectExchange orderExchange
    ) {
        return BindingBuilder
                .bind(orderCreatedQueueCommand)
                .to(orderExchange)
                .with(ORDER_CREATED_RK);
    }

    // Cola escuchará "order.status.update" del MS original
    @Bean
    public Queue orderStatusUpdateQueueCommand() {
        return new Queue(ORDER_STATUS_UPDATE_QUEUE_COMMAND, true);
    }

    @Bean
    public Binding orderStatusUpdateQueueCommandBinding(
            Queue orderStatusUpdateQueueCommand,
            DirectExchange orderExchange
    ) {
        return BindingBuilder
                .bind(orderStatusUpdateQueueCommand)
                .to(orderExchange)
                .with(ORDER_STATUS_UPDATE_RK);
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
