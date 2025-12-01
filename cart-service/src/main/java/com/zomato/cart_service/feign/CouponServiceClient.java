package com.zomato.cart_service.feign;

import com.zomato.cart_service.dto.feign.fetch.CouponBridgeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@FeignClient(name="coupon-service",url="localhost:8084/api/coupon")
public interface CouponServiceClient {

    @GetMapping("/get/{couponCode}/{restaurantId}")
    public CouponBridgeDto getCouponWithCouponCodeAndRestaurant(@PathVariable("couponCode") String couponCode, @PathVariable("restaurantId") UUID restaurantId);

    @PutMapping("/update/currentUsage/{couponId}")
    public boolean updateCurrentUsage(@PathVariable("couponId") UUID couponId);
}
