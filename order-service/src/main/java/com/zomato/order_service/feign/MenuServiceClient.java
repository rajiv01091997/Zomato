package com.zomato.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name="menu-service",url="localhost:8081/api/menu")
public interface MenuServiceClient {

    @GetMapping("/get/restaurantId/{itemId}")
    public Optional<UUID> getRestaurantIdByItemId(@PathVariable("itemId") UUID itemId);

    @GetMapping("/get/price/{itemId}")
    public Optional<Double> getPriceByItemId(@PathVariable("itemId") UUID itemId);

    @GetMapping("/get/availability/{itemId}")
    public Optional<Boolean> getAvailabilityByItemId(@PathVariable("itemId") UUID itemId);

    @GetMapping("/get/itemName/{itemId}")
    public Optional<String> getItemNameByItemId(@PathVariable("itemId") UUID itemId);
}