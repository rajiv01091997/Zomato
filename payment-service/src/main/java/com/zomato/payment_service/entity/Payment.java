package com.zomato.payment_service.entity;

import com.zomato.payment_service.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Entity
public class Payment {
    @Id
    private String paymentId;
    private String orderId;
    private PaymentStatus status;
    private double amount;
    private String currency;
    private String method;
    private String bank;
    private String bankTransactionId;
    @Column(updatable = false)
    private Instant time;

    @PrePersist
    protected void onCreate() {
        this.time = Instant.now();  // ✅ DB automatically set karega
    }
}
