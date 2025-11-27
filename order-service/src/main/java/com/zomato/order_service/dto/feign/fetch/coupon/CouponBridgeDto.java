package com.zomato.order_service.dto.feign.fetch.coupon;



import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponBridgeDto {
    private UUID id;
    private UUID restaurantId;
    private String couponCode;
    private Double minOrderValue;
    private Long overallUsageCount;
    private Long currentUsageCount;
    private DiscountType discountType;
    private Double discountValue;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
