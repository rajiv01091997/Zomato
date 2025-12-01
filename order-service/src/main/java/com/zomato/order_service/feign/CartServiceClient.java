package com.zomato.order_service.feign;

import com.zomato.order_service.dto.feign.fetch.cart.CartBridgeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@FeignClient(name="cart-service",url="localhost:8082/api/cart")
public interface CartServiceClient {


    @GetMapping("/isSameAmount/{cartId}")
    public boolean isSameCurrentAmountFromMenuToCartAmount(@PathVariable("cartId") UUID cartId);

    @GetMapping("/get/cart/{cartId}")
    public CartBridgeDto getCartDetails(@PathVariable("cartId") UUID cartId);

    @PutMapping("/update/status/{cartId}")
    public void changeStatus(@PathVariable("cartId") UUID cartId);
}
