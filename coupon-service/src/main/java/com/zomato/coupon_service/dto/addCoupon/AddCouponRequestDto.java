package com.zomato.coupon_service.dto.addCoupon;

import com.zomato.coupon_service.eums.DiscountType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddCouponRequestDto {
    private String couponCode;
    private Double minOrderValue;
    private Long overallUsageCount;
    //private Long currentUsageCount;
    private DiscountType discountType;
    private Double discountValue;
    private Boolean isActive;
}
