package com.zomato.order_service.dto.feign.fetch.invoice;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ItemDto {
    private String name;
    private int quantity;
    private double unitPrice;
    private double subtotal;
}
