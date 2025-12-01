package com.zomato.coupon_service.service;

import com.zomato.coupon_service.dto.feign.CouponBridgeDto;
import com.zomato.coupon_service.entity.Coupon;
import com.zomato.coupon_service.repository.CouponRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UtilityCouponService {

    @Autowired
    private CouponRepository repository;


    public CouponBridgeDto getCouponDetails(String couponCode, UUID restaurantId) {

        Coupon coupon = repository.findByCouponCodeAndRestaurantId(couponCode, restaurantId).get();
        if (coupon != null) {
            return CouponBridgeDto.builder()
                    .id(coupon.getId())
                    .restaurantId(coupon.getRestaurantId())
                    .couponCode(coupon.getCouponCode())
                    .discountType(coupon.getDiscountType())
                    .discountValue(coupon.getDiscountValue())
                    .minOrderValue(coupon.getMinOrderValue())
                    .currentUsageCount(coupon.getCurrentUsageCount())
                    .overallUsageCount(coupon.getOverallUsageCount())
                    .isActive(coupon.getIsActive())
                    .createdAt(coupon.getCreatedAt())
                    .updatedAt(coupon.getUpdatedAt())
                    .build();

        } else
            return null;
    }
    public List<Coupon> getAllByRestaurantId(UUID restaurantId) {
        return repository.findAllByRestaurantId(restaurantId);
    }
    @Transactional
    public boolean updateCurrentUsage(UUID couponId)
    {
        Optional<Coupon> couponOpt = repository.findById(couponId);
        if (couponOpt.isEmpty()) {
            throw new RuntimeException("No coupon found: " + couponId);
        }

        Coupon coupon = couponOpt.get();

        // ✅ RACE CONDITION FIX: Atomic check
        if (coupon.getCurrentUsageCount() >= coupon.getOverallUsageCount()) {
            return false;  // Limit reached
        }
        coupon.setCurrentUsageCount(coupon.getCurrentUsageCount()+1);
        repository.save(coupon);
        return true;
    }
}
