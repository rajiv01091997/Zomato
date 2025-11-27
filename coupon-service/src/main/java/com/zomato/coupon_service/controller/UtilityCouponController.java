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

    @GetMapping("/get/{couponCode}/{restaurantId}")
    public ResponseEntity<?> getCouponWithCouponCodeAndRestaurant(@PathVariable("couponCode") String couponCode,@PathVariable("restaurantId") UUID restaurantId)
    {
        return new ResponseEntity<>(service.getCouponDetails(couponCode, restaurantId), HttpStatus.OK);
    }

    @GetMapping("/get/{restaurantId}")
    public ResponseEntity<?> getAllByRestaurantId(@PathVariable("restaurantId") UUID restaurantId)
    {
        return new ResponseEntity<>(service.getAllByRestaurantId(restaurantId), HttpStatus.OK);
    }

}
