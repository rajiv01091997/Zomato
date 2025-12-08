package com.zomato.order_service.dto.kafka.payment;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderPaymentDto {
    private String paymentId;
    private String orderId;
    private PaymentStatus status;
}
