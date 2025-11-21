package com.zomato.coupon_service.service;

import com.zomato.coupon_service.entity.Coupon;
import com.zomato.coupon_service.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilityCouponService {

    @Autowired
    private CouponRepository repository;

    //utility methods for order-service
    public boolean isCouponWithGivenCodePresentForGivenRestaurant(String couponCode, UUID restaurantId)
    {
        Coupon coupon= repository.findByCouponCodeAndRestaurantId(couponCode,restaurantId).get();
        if(coupon==null)
            return false;
        else
            return true;
    }
    public UUID getIdOfCouponWithGivenCouponCodeAndRestaurantId(String couponCode,UUID restaurantId)
    {
        Coupon coupon= repository.findByCouponCodeAndRestaurantId(couponCode,restaurantId).get();
        return coupon.getId();
    }
    public boolean isCouponWithGivenIdActive(UUID id)
    {
        Coupon coupon=repository.findById(id).get();
        if(coupon.getIsActive()==true)
            return true;
        else
            return false;
    }
//    public boolean isCouponAllowedOnGivenAmount(UUID id,double totalAmount)
//    {
//        Coupon coupon=repository.findById(id).get();
//        if(coupon.getMinOrderValue()<=totalAmount)
//            return true;
//        else
//            return false;
//    }
    public boolean IsOverallUsageGreaterThanCurrentUsageForGivenCoupon(UUID id)
    {
        Coupon coupon=repository.findById(id).get();
        if(coupon.getCurrentUsageCount()<coupon.getOverallUsageCount())
            return true;
        else
            return false;
    }
    public String getDiscountType(UUID id)
    {
        Coupon coupon=repository.findById(id).get();
        return coupon.getDiscountType().name();
    }
    public double getDiscountValue(UUID id)
    {
        Coupon coupon=repository.findById(id).get();
        return coupon.getDiscountValue();
    }
}
