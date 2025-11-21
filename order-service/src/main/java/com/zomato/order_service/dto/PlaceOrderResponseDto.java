package com.zomato.order_service.dto;

import com.zomato.order_service.enums.OrderStatus;
import com.zomato.order_service.enums.PaymentStatus;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class PlaceOrderResponseDto {
    private UUID id;
    private UUID cartId;
    private UUID customerId;
    private UUID restaurantId;
    private UUID riderId;
    private String couponCode;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime orderTime;
    private LocalDateTime deliveryTime;
    private double totalAmount;
    private String deliveryAddress;
    private String specialInstructions;

}
