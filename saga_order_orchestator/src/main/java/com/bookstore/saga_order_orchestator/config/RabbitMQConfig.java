package com.bookstore.saga_order_orchestator.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class RabbitMQConfig {

    // Exchange donde Order MS publica "OrderCreated"
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_CREATED_RK = "order.created";

    // Exchange de Payment
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_PROCESS_RK = "payment.process";
    public static final String PAYMENT_COMPLETED_RK = "payment.completed";
    public static final String PAYMENT_FAILED_RK = "payment.failed";

    // Exchange de Inventory
    public static final String INVENTORY_EXCHANGE = "inventory.exchange";
    public static final String INVENTORY_RESERVE_RK = "inventory.reserve";
    public static final String INVENTORY_RESERVED_RK = "inventory.reserved";
    public static final String INVENTORY_RESERVE_FAILED_RK = "inventory.reserve.failed";

    // Exchange donde Order MS escucha actualizaciones de orden
    public static final String ORDER_STATUS_UPDATE_RK = "order.status.update";

    // Cola para que el Orchestrator escuche "OrderCreated"
    public static final String ORCH_ORDER_CREATED_QUEUE = "orchestrator.ordercreated.queue";

    // Cola para que el Orchestrator escuche "PaymentCompleted" y "PaymentFailed"
    public static final String ORCH_PAYMENT_COMPLETED_QUEUE = "orchestrator.paymentcompleted.queue";
    public static final String ORCH_PAYMENT_FAILED_QUEUE = "orchestrator.paymentfailed.queue";

    // Cola para que el Orchestrator escuche "InventoryReserved" y "InventoryReservationFailed"
    public static final String ORCH_INVENTORY_RESERVED_QUEUE = "orchestrator.inventoryreserved.queue";
    public static final String ORCH_INVENTORY_RESERVE_FAILED_QUEUE = "orchestrator.inventoryfailed.queue";

    // Exchanges
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public DirectExchange inventoryExchange() {
        return new DirectExchange(INVENTORY_EXCHANGE);
    }

    // 2. Colas para que el orquestador escuche
    @Bean
    public Queue orchOrderCreatedQueue() {
        return new Queue(ORCH_ORDER_CREATED_QUEUE, true);
    }

    @Bean
    public Queue orchPaymentCompletedQueue() {
        return new Queue(ORCH_PAYMENT_COMPLETED_QUEUE, true);
    }

    @Bean
    public Queue orchPaymentFailedQueue() {
        return new Queue(ORCH_PAYMENT_FAILED_QUEUE, true);
    }

    @Bean
    public Queue orchInventoryReservedQueue() {
        return new Queue(ORCH_INVENTORY_RESERVED_QUEUE, true);
    }

    @Bean
    public Queue orchInventoryReserveFailedQueue() {
        return new Queue(ORCH_INVENTORY_RESERVE_FAILED_QUEUE, true);
    }

    // Bindings
    @Bean
    public Binding orderCreatedBinding(Queue orchOrderCreatedQueue, DirectExchange orderExchange) {
        return BindingBuilder
                .bind(orchOrderCreatedQueue)
                .to(orderExchange)
                .with(ORDER_CREATED_RK);
    }

    @Bean
    public Binding paymentCompletedBinding(Queue orchPaymentCompletedQueue, DirectExchange paymentExchange) {
        return BindingBuilder
                .bind(orchPaymentCompletedQueue)
                .to(paymentExchange)
                .with(PAYMENT_COMPLETED_RK);
    }

    @Bean
    public Binding paymentFailedBinding(Queue orchPaymentFailedQueue, DirectExchange paymentExchange) {
        return BindingBuilder
                .bind(orchPaymentFailedQueue)
                .to(paymentExchange)
                .with(PAYMENT_FAILED_RK);
    }

    @Bean
    public Binding inventoryReservedBinding(Queue orchInventoryReservedQueue, DirectExchange inventoryExchange) {
        return BindingBuilder
                .bind(orchInventoryReservedQueue)
                .to(inventoryExchange)
                .with(INVENTORY_RESERVED_RK);
    }

    @Bean
    public Binding inventoryFailedBinding(Queue orchInventoryReserveFailedQueue, DirectExchange inventoryExchange) {
        return BindingBuilder
                .bind(orchInventoryReserveFailedQueue)
                .to(inventoryExchange)
                .with(INVENTORY_RESERVE_FAILED_RK);
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
