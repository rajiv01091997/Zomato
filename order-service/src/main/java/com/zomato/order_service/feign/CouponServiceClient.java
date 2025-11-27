package com.zomato.order_service.feign;

import com.zomato.order_service.dto.feign.fetch.coupon.CouponBridgeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name="coupon-service",url="localhost:8084/api/coupon")
public interface CouponServiceClient {
    @GetMapping("/get/{couponCode}/{restaurantId}")
    public CouponBridgeDto getCouponWithCouponCodeAndRestaurant(@PathVariable("couponCode") String couponCode, @PathVariable("restaurantId") UUID restaurantId);
}
