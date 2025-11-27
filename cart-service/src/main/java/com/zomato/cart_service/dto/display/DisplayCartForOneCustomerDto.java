package com.zomato.cart_service.dto.display;

import com.zomato.cart_service.enums.CartStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DisplayCartForOneCustomerDto {
    private UUID cartId;
    private UUID customerId;
    private UUID restaurantId;
    private CartStatus status;
    private double totalAmount;
    private String couponCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<DisplayItemDto> displayItemList;
}
