package com.bookstore.microservice.bookcatalog.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class RabbitMQConfig {

    public static final String BOOK_CATALOG_EXCHANGE = "book.catalog.exchange";
    public static final String BOOK_STOCK_UPDATE_RK = "book.stock.update";

    public static final String BOOK_STOCK_UPDATE_QUEUE = "book.stock.update.queue";

    @Bean
    public DirectExchange bookCatalogExchange() {
        return new DirectExchange(BOOK_CATALOG_EXCHANGE);
    }

    @Bean
    public Queue bookStockUpdateQueue() {
        return new Queue(BOOK_STOCK_UPDATE_QUEUE, true);
    }

    @Bean
    public Binding bookStockUpdateBinding(Queue bookStockUpdateQueue, DirectExchange bookCatalogExchange) {
        return BindingBuilder
                .bind(bookStockUpdateQueue)
                .to(bookCatalogExchange)
                .with(BOOK_STOCK_UPDATE_RK);
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
