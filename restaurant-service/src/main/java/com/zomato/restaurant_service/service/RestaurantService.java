package com.zomato.restaurant_service.service;

import com.zomato.restaurant_service.dto.RestaurantsListDto;
import com.zomato.restaurant_service.feign.UserRestaurantServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService implements RestaurantServiceInterface{

    @Autowired
    private UserRestaurantServiceClient userRestaurantServiceClient;

    public List<RestaurantsListDto> getRestaurantList()
    {
       return userRestaurantServiceClient.getRestaurant();
    }
}
