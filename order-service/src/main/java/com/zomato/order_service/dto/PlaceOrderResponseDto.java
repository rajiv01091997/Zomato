package com.zomato.order_service.dto;

import com.zomato.order_service.dto.kafka.payment.PaymentStatus;
import com.zomato.order_service.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Builder
public class PlaceOrderResponseDto {
    private UUID id;
    private String orderId;
    private UUID cartId;
    private UUID customerId;
    private UUID restaurantId;
    private UUID riderId;
    private String couponCode;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime orderTime;
    private double totalAmount;
    private String deliveryAddress;
    private String specialInstructions;

}
