package com.zomato.order_service.dto.feign.fetch.cart;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartBridgeDto {
    private UUID cartId;
    private UUID customerId;
    private UUID restaurantId;
    private CartStatus status;
    private double totalAmount;
    private double grossAmount;
    private String couponCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ItemBridgeDto> itemBridgeDtoList;
}
