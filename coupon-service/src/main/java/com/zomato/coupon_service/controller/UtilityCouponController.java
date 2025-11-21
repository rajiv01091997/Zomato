package com.zomato.coupon_service.controller;


import com.zomato.coupon_service.service.UtilityCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupon")
public class UtilityCouponController {
    @Autowired
    private UtilityCouponService service;

    @GetMapping("/isPresent/{couponCode}/{restaurantId}")
    public ResponseEntity<?> isCouponWithGivenCodePresentForGivenRestaurant(@PathVariable("couponId") String couponCode,@PathVariable("restaurantId") UUID restaurantId)
    {
        return new ResponseEntity<>(service.isCouponWithGivenCodePresentForGivenRestaurant(couponCode, restaurantId), HttpStatus.OK);
    }

    @GetMapping("/get/{couponCode}/{restaurantId}")
    public ResponseEntity<?> getIdOfCouponWithGivenCouponCodeAndRestaurantId(@PathVariable("couponCode") String couponCode,@PathVariable("restaurantId") UUID restaurantId)
    {
        return new ResponseEntity<>(service.getIdOfCouponWithGivenCouponCodeAndRestaurantId(couponCode, restaurantId), HttpStatus.OK);
    }

    @GetMapping("/isActive/{couponId}")
    public ResponseEntity<?> isCouponWithGivenIdActive(@PathVariable("couponId") UUID couponId)
    {
        return new ResponseEntity<>(service.isCouponWithGivenIdActive(couponId), HttpStatus.OK);
    }

//    @GetMapping("/isTotalAmountSuffice/{couponId}/{totalAmount}")
//    public ResponseEntity<?> isCouponAllowedOnGivenAmount(@PathVariable("couponId") UUID couponId,@PathVariable("totalAmount") double totalAmount)
//    {
//        return new ResponseEntity<>(service.isCouponAllowedOnGivenAmount(couponId,totalAmount), HttpStatus.OK);
//    }
    @GetMapping("/isUsageLeft/{couponId}")
    public ResponseEntity<?> IsOverallUsageGreaterThanCurrentUsageForGivenCoupon(@PathVariable("couponId") UUID couponId)
    {
        return new ResponseEntity<>(service.IsOverallUsageGreaterThanCurrentUsageForGivenCoupon(couponId), HttpStatus.OK);
    }

    @GetMapping("/getDiscountType/{couponId}")
    public ResponseEntity<?> getDicountType(@PathVariable("couponId") UUID couponId)
    {
        return new ResponseEntity<>(service.getDiscountType(couponId), HttpStatus.OK);
    }

    @GetMapping("/getDiscountValue/{couponId}")
    public ResponseEntity<?> getDiscountValue(@PathVariable("couponId") UUID couponId)
    {
        return new ResponseEntity<>(service.getDiscountValue(couponId), HttpStatus.OK);
    }

}
