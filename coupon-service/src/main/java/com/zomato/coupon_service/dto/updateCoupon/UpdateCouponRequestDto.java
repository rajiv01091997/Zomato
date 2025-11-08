package com.zomato.coupon_service.dto.updateCoupon;

import com.zomato.coupon_service.eums.DiscountType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCouponRequestDto {
    private UUID id;
    private String couponCode;
    private Double minOrderValue;
    private Long overallUsageCount;
    //private Long currentUsageCount;
    private DiscountType discountType;
    private Double discountValue;
    private Boolean isActive;
}
