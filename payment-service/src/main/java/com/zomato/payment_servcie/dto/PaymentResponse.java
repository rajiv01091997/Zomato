package com.zomato.payment_servcie.dto;

import com.zomato.payment_servcie.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String orderId;
    private String linkId;
    private String paymentLink;
    private PaymentStatus status;

}
