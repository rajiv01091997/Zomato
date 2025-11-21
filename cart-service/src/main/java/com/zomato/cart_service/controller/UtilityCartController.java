package com.zomato.cart_service.controller;

import com.zomato.cart_service.service.UtilityCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class UtilityCartController {
    @Autowired
    private UtilityCartService utilityCartService;

    @GetMapping("/isPresent/{cartId}")
    public ResponseEntity<?> isPresentWithCartId(@PathVariable("cartId") UUID cartId)
    {
        return new ResponseEntity<>(utilityCartService.isPresentWithCartId(cartId), HttpStatus.OK);
    }

    @GetMapping("/isActive/{cartId}")
    public ResponseEntity<?> isActive(@PathVariable("cartId") UUID cartId)
    {
        return new ResponseEntity<>(utilityCartService.isActive(cartId), HttpStatus.OK);
    }
    @GetMapping("/isSameAmount/{cartId}")
    public ResponseEntity<?> isSameCurrentAmountFromMenuToCartAmount(@PathVariable("cartId") UUID cartId)
    {
        return new ResponseEntity<>(utilityCartService.isSameCurrentAmountFromMenuToCartAmount(cartId), HttpStatus.OK);
    }

    @GetMapping("/getCustomerId/{cartId}")
    public ResponseEntity<?> getCustomerIdWithCartId(@PathVariable("cartId") UUID cartId)
    {
        return new ResponseEntity<>(utilityCartService.getCustomerIdWithCartId(cartId), HttpStatus.OK);
    }
    @GetMapping("/getCouponCode/{cartId}")
    public ResponseEntity<?> getCouponCode(@PathVariable("cartId") UUID cartId)
    {
        return new ResponseEntity<>(utilityCartService.getCouponCode(cartId), HttpStatus.OK);
    }
    @GetMapping("/isCouponApplied/{cartId}")
    public ResponseEntity<?> isCouponAppliedOnCart(@PathVariable("cartId") UUID cartId)
    {
        return new ResponseEntity<>(utilityCartService.isCouponAppliedOnCart(cartId), HttpStatus.OK);
    }
    @GetMapping("/getRestaurantId/{cartId}")
    public ResponseEntity<?> getRestaurantId(@PathVariable("cartId") UUID cartId)
    {
        return new ResponseEntity<>(utilityCartService.getRestaurantId(cartId), HttpStatus.OK);
    }
    @GetMapping("/getTotalAmount/{cartId}")
    public ResponseEntity<?> getTotalAmount(@PathVariable("cartId") UUID cartId)
    {
        return new ResponseEntity<>(utilityCartService.getTotalAmount(cartId), HttpStatus.OK);
    }



}
