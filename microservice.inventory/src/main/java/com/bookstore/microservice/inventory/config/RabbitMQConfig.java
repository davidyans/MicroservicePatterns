package com.bookstore.microservice.inventory.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class RabbitMQConfig {

    public static final String INVENTORY_EXCHANGE = "inventory.exchange";

    public static final String INVENTORY_RESERVE_RK = "inventory.reserve";
    public static final String INVENTORY_RESERVED_RK = "inventory.reserved";
    public static final String INVENTORY_RESERVE_FAILED_RK = "inventory.reserve.failed";

    public static final String INVENTORY_RESERVE_QUEUE = "inventory.reserve.queue";

    public static final String BOOK_CATALOG_EXCHANGE = "book.catalog.exchange";
    public static final String BOOK_STOCK_UPDATE_RK = "book.stock.update";

    @Bean
    public DirectExchange inventoryExchange() {
        return new DirectExchange(INVENTORY_EXCHANGE);
    }

    @Bean
    public Queue inventoryReserveQueue() {
        return new Queue(INVENTORY_RESERVE_QUEUE, true);
    }

    @Bean
    public Binding inventoryReserveBinding(Queue inventoryReserveQueue, DirectExchange inventoryExchange) {
        return BindingBuilder
                .bind(inventoryReserveQueue)
                .to(inventoryExchange)
                .with(INVENTORY_RESERVE_RK);
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
