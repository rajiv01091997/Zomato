package com.zomato.cart_service.dto.add;

import lombok.*;


import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddCartRequestDto {
    private UUID restaurantId;
    private String couponCode;
    private List<AddItemDto> addItemListDto;
}
