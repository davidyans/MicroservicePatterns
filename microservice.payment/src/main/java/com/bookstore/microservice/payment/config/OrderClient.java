package com.bookstore.microservice.payment.config;

import com.bookstore.microservice.payment.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "orderClient", url = "${services.order.url}")
public interface OrderClient {

    @GetMapping("/api/orders/{id}")
    OrderDTO getOrderById(@PathVariable("id") Integer id);
}
