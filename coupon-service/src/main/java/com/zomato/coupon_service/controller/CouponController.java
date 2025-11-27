package com.zomato.coupon_service.controller;

import com.zomato.coupon_service.dto.addCoupon.AddCouponRequestDto;
import com.zomato.coupon_service.dto.updateCoupon.UpdateCouponRequestDto;
import com.zomato.coupon_service.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @PostMapping("/add")
    public ResponseEntity<?> save(@RequestBody AddCouponRequestDto addCouponRequestDto)
    {
        return new ResponseEntity<>(couponService.save(addCouponRequestDto), HttpStatus.CREATED);
    }
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody UpdateCouponRequestDto updateCouponRequestDto)
    {
        return new ResponseEntity<>(couponService.update(updateCouponRequestDto), HttpStatus.OK);
    }

    @PutMapping("/update/expiry/{id}")
    public ResponseEntity<?> updateToExpiredByCouponId(@PathVariable("id") UUID id)
    {
        return new ResponseEntity<>(couponService.updateToExpiredByCouponId(id), HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllByRestaurantId()
    {
        return new ResponseEntity<>(couponService.getAllByRestaurantId(), HttpStatus.OK);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteByCouponId(@PathVariable("id") UUID id)
    {
        return new ResponseEntity<>(couponService.deleteByCouponId(id),HttpStatus.OK);
    }
}
