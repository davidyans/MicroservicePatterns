package com.bookstore.gateway_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RestController
public class OrderComposerController {

    private final WebClient webClient;

    @Value("${route.orders-service.uri}")
    private String orderServiceUrl;

    @Value("${route.payments-service.uri}")
    private String paymentServiceUrl;

    @Value("${route.books-service.uri}")
    private String bookServiceUrl;

    public OrderComposerController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/api/composer/orders/{orderId}")
    public ResponseEntity<?> findOrder(@PathVariable Integer orderId) {
        try {
            Map<String, Object> order = webClient.get()
                    .uri(orderServiceUrl + "/api/orders/" + orderId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Order not found"));
            }

            List<Map<String, Object>> orderDetails = webClient.get()
                    .uri(orderServiceUrl + "/api/orders/" + orderId + "/details")
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            if (orderDetails == null) {
                orderDetails = List.of();
            }

            for (Map<String, Object> detail : orderDetails) {
                Integer bookId = (Integer) detail.get("bookId");
                Map<String, Object> book = webClient.get()
                        .uri(bookServiceUrl + "/api/books/" + bookId)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (book != null && book.containsKey("title")) {
                    detail.put("title", book.get("title"));
                }
            }

            Map<String, Object> payment = webClient.get()
                    .uri(paymentServiceUrl + "/api/payments/" + orderId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            Map<String, Object> composedResponse = Map.of(
                    "orderId", order.get("orderId"),
                    "orderDate", order.get("orderDate"),
                    "status", order.get("status"),
                    "total", order.get("total"),
                    "paymentMethod", payment != null ? payment.get("payMethod") : "Unknown",
                    "orderDetails", orderDetails
            );

            return ResponseEntity.ok(composedResponse);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "An error occurred while composing the response",
                    "details", ex.getMessage()
            ));
        }
    }

}
