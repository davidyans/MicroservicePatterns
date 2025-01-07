package com.bookstore.microservice.payment.repository;

import com.bookstore.microservice.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
