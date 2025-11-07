package com.zomato.cart_service.dto.add;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddItemListDto {
    private UUID itemId;
    private String itemName;
    private int quantity;
    private double price;
}
