package com.zomato.payment_servcie.repository;


import com.zomato.payment_servcie.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
