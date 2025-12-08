package com.zomato.cart_service.dto.update;

import com.zomato.cart_service.dto.add.AddItemDto;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateCartRequestDto {
    private UUID cartId;
    private String couponCode;
    private List<UpdateItemDto> updateItemDtos;
}
