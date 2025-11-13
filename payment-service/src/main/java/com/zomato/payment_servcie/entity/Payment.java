package com.zomato.payment_servcie.entity;

import com.zomato.payment_servcie.enums.PaymentStatus;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Entity
public class Payment {
    private UUID id;
    private UUID orderId;
    private PaymentStatus status;
    private double amount;
    private String currency;
    private Instant time;
}
