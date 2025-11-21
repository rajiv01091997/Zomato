package com.zomato.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name="coupon-service",url="localhost:8084/api/coupon")
public interface CouponServiceClient {
    @GetMapping("/isPresent/{couponCode}/{restaurantId}")
    public boolean isCouponWithGivenCodePresentForGivenRestaurant(@PathVariable("couponId") String couponCode, @PathVariable("restaurantId") UUID restaurantId);


    @GetMapping("/get/{couponCode}/{restaurantId}")
    public UUID getIdOfCouponWithGivenCouponCodeAndRestaurantId(@PathVariable("couponCode") String couponCode,@PathVariable("restaurantId") UUID restaurantId);


    @GetMapping("/isActive/{couponId}")
    public boolean isCouponWithGivenIdActive(@PathVariable("couponId") UUID couponId);


//    @GetMapping("/isTotalAmountSuffice/{couponId}/{totalAmount}")
//    public boolean isCouponAllowedOnGivenAmount(@PathVariable("couponId") UUID couponId, @PathVariable("totalAmount") double totalAmount);

    @GetMapping("/isUsageLeft/{couponId}")
    public boolean IsOverallUsageGreaterThanCurrentUsageForGivenCoupon(@PathVariable("couponId") UUID couponId);


    @GetMapping("/getDiscountType/{couponId}")
    public String getDicountType(@PathVariable("couponId") UUID couponId);


    @GetMapping("/getDiscountValue/{couponId}")
    public double getDiscountValue(@PathVariable("couponId") UUID couponId);

}
