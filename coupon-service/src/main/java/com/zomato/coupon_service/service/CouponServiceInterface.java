package com.zomato.coupon_service.service;

import com.zomato.coupon_service.dto.addCoupon.AddCouponRequestDto;
import com.zomato.coupon_service.dto.addCoupon.AddCouponResponseDto;
import com.zomato.coupon_service.dto.updateCoupon.UpdateCouponRequestDto;
import com.zomato.coupon_service.dto.updateCoupon.UpdateCouponResponseDto;
import com.zomato.coupon_service.entity.Coupon;

import java.util.List;
import java.util.UUID;

public interface CouponServiceInterface {
    AddCouponResponseDto save(AddCouponRequestDto addCouponRequestDto);
    UpdateCouponResponseDto update(UpdateCouponRequestDto updateCouponRequestDto);
    List<Coupon> getAllByRestaurantId();

    //for access by anyone if anyone needs list of coupons
    List<Coupon> getAllByRestaurantId(UUID restaurantId);

    UpdateCouponResponseDto updateToExpiredByCouponId(UUID id);
    String deleteByCouponId(UUID id);
}
