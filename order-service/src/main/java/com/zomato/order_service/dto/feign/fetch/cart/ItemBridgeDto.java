package com.zomato.order_service.dto.feign.fetch.cart;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemBridgeDto {
    private UUID itemId;
    private String itemName;
    private int quantity;
    private double price;
}
