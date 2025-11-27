package com.zomato.cart_service.dto.feign.expose;

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
