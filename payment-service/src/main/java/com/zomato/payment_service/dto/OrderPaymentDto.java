package com.zomato.payment_service.dto;

import com.zomato.payment_service.enums.PaymentStatus;
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
