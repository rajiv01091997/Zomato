package com.zomato.cart_service.dto.display;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DisplayItemDto {
    private UUID itemId;
    private String itemName;
    private int quantity;
    private double price;
}
