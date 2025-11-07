package com.zomato.cart_service.dto.add;

import com.zomato.cart_service.entity.Item;
import lombok.*;


import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddCartRequestDto {

    private UUID customerId;
    private UUID restaurantId;
    private String couponCode;

    private List<AddItemListDto> addItemListDto;
}
