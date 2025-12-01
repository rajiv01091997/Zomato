package com.zomato.cart_service.controller;

import com.zomato.cart_service.service.UtilityCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class UtilityCartController {
    @Autowired
    private UtilityCartService utilityCartService;

    @GetMapping("/isSameAmount/{cartId}")
    public ResponseEntity<?> isSameCurrentAmountFromMenuToCartAmount(@PathVariable("cartId") UUID cartId)
    {
        return new ResponseEntity<>(utilityCartService.isSameCurrentAmountFromMenuToCartAmount(cartId), HttpStatus.OK);
    }


    @GetMapping("/get/cart/{cartId}")
    public ResponseEntity<?> getCartDetails(@PathVariable("cartId") UUID cartId)
    {
        return new ResponseEntity<>(utilityCartService.getCartDetails(cartId),HttpStatus.OK);
    }

    @PutMapping("/update/status/{cartId}")
    public void changeStatus(@PathVariable("cartId") UUID cartId)
    {
        utilityCartService.changeStatus(cartId);
    }


}
