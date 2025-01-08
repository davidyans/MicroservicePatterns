package com.bookstore.order_command.services;

import com.bookstore.order_command.dto.OrderDTO;

public interface OrderCommandService {
    OrderDTO createOrder(OrderDTO orderDTO);

}
