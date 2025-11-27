package com.zomato.user_service.controller;

import com.zomato.user_service.service.UtilityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UtilityUserController {
    @Autowired
    private UtilityUserService userService;
    //bridge method for restaurant-service
    @GetMapping("/get/restaurant-details")
    public ResponseEntity<?> getLongitudeOfUser()
    {
        return new ResponseEntity<>(userService.getRestaurantsList(), HttpStatus.OK);
    }

    //for order service
    @GetMapping("/get/latitude/{userId}")
    public ResponseEntity<?> getLatitudeOfUser(@PathVariable("userId") UUID userId)
    {
        return new ResponseEntity<>(userService.getLatitude(userId), HttpStatus.OK);
    }
    @GetMapping("/get/longitude/{userId}")
    public ResponseEntity<?> getLongitudeOfUser(@PathVariable("userId") UUID userId)
    {
        return new ResponseEntity<>(userService.getLatitude(userId), HttpStatus.OK);
    }
    @GetMapping("/get/address/{userId}")
    public ResponseEntity<?> getAddressOfUser(@PathVariable("userId") UUID userId)
    {
        return new ResponseEntity<>(userService.getAddressByUserId(userId), HttpStatus.OK);
    }
    @GetMapping("/get/userName/{userId}")
    public ResponseEntity<?> getUserNameOfUser(@PathVariable("userId") UUID userId)
    {
        return new ResponseEntity<>(userService.getUserName(userId), HttpStatus.OK);
    }
    @GetMapping("/get/restaurantName/{userId}")
    public ResponseEntity<?> getRestaurantNameOfUser(@PathVariable("userId") UUID userId)
    {
        return new ResponseEntity<>(userService.getRestaurantName(userId), HttpStatus.OK);
    }

    @GetMapping("/get/email/{userId}")
    public ResponseEntity<?> getEmailOfUser(@PathVariable("userId") UUID userId)
    {
        return new ResponseEntity<>(userService.getEmail(userId), HttpStatus.OK);
    }
    @GetMapping("/get/phoneNumber/{userId}")
    public ResponseEntity<?> getPhoneNumberOfUser(@PathVariable("userId") UUID userId)
    {
        return new ResponseEntity<>(userService.getPhoneNumber(userId), HttpStatus.OK);
    }




}
