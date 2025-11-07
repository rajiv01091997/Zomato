package com.zomato.restaurant_service.feign;

import com.zomato.restaurant_service.dto.RestaurantsListDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "user-service",url="localhost:8080/user")
public interface UserRestaurantServiceClient {

    @GetMapping("/get/restaurant-details")
    public List<RestaurantsListDto> getRestaurant();
}
