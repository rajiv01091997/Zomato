package com.zomato.mail_service.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@FeignClient(name="users-service",url="localhost:8080/api/user")
public interface UserServiceClient {

    @GetMapping("/get/latitude/{userId}")
    public Double getLatitudeOfUser(@PathVariable("userId") UUID userId);
    @GetMapping("/get/longitude/{userId}")
    public Double getLongitudeOfUser(@PathVariable("userId") UUID userId);

    @GetMapping("/get/address/{userId}")
    public String getAddressOfUser(@PathVariable("userId") UUID userId);

    @GetMapping("/get/userName/{userId}")
    public String getUserNameOfUser(@PathVariable("userId") UUID userId);

    @GetMapping("/get/restaurantName/{userId}")
    public String getRestaurantNameOfUser(@PathVariable("userId") UUID userId);

    @GetMapping("/get/email/{userId}")
    public String getEmailOfUser(@PathVariable("userId") UUID userId);

    @GetMapping("/get/phoneNumber/{userId}")
    public String getPhoneNumberOfUser(@PathVariable("userId") UUID userId);

    @PutMapping("update/availabilityStatus/{userId}")
    public void updateAvailabilityStatusOfRider(UUID userId);
}
