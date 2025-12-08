package com.zomato.order_service.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class UpdateStatus {
    private String orderId;
    private String toStatus;
    private String otp;

}
