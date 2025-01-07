package com.bookstore.microservice.payment.listeners;

import com.bookstore.microservice.payment.config.RabbitMQConfig;
import com.bookstore.microservice.payment.domain.Payment;
import com.bookstore.microservice.payment.events.ProcessPaymentCommand;
import com.bookstore.microservice.payment.events.PaymentCompletedEvent;
import com.bookstore.microservice.payment.events.PaymentFailedEvent;
import com.bookstore.microservice.payment.repository.PaymentRepository;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProcessPaymentListener {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_PROCESS_QUEUE)
    public void handleProcessPaymentCommand(ProcessPaymentCommand command) {

        System.out.println("Received ProcessPaymentCommand for OrderID: " + command.getOrderId());

        try {
            // 1. Simular lógica de cobro o validación
            Payment payment = new Payment();
            payment.setOrderId(command.getOrderId());
            payment.setAmount(command.getAmount());
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPayMethod(command.getPayMethod() != null ? command.getPayMethod() : "CREDIT_CARD");

            if (command.getAmount() == null || command.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                // Invalid amount -> falla
                payment.setStatus("FAILED");
            } else {
                // Pago exitoso (ejemplo trivial)
                payment.setStatus("COMPLETED");
            }

            // 2. Guardar en BD
            Payment saved = paymentRepository.save(payment);

            // 3. Publicar el evento de resultado
            if ("COMPLETED".equals(saved.getStatus())) {
                PaymentCompletedEvent completedEvent = new PaymentCompletedEvent(
                        saved.getOrderId(),
                        saved.getPaymentId(),
                        saved.getStatus()
                );
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.PAYMENT_EXCHANGE,
                        RabbitMQConfig.PAYMENT_COMPLETED_RK,
                        completedEvent
                );

                System.out.println(">>> PaymentCompletedEvent published for OrderID: " + saved.getOrderId());
            } else {
                PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                        saved.getOrderId(),
                        "Payment was not possible or amount is invalid."
                );
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.PAYMENT_EXCHANGE,
                        RabbitMQConfig.PAYMENT_FAILED_RK,
                        failedEvent
                );

                System.out.println(">>> PaymentFailedEvent published for OrderID: " + saved.getOrderId());
            }

        } catch (Exception e) {
            PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                    command.getOrderId(),
                    "Unexpected error: " + e.getMessage()
            );
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_EXCHANGE,
                    RabbitMQConfig.PAYMENT_FAILED_RK,
                    failedEvent
            );
            System.out.println(">>> PaymentFailedEvent published due to exception. OrderID: " + command.getOrderId());
        }
    }
}
