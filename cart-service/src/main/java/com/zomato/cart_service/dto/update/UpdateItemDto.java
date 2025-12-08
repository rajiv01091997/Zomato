package com.zomato.cart_service.dto.update;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateItemDto {
    private UUID itemId;
    private String itemName;
    private int quantity;
    private double price;
}
