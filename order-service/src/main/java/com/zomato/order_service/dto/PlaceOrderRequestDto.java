package com.zomato.order_service.dto;


import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class PlaceOrderRequestDto {
    private UUID cartId;
    private String specialInstructions;

}
