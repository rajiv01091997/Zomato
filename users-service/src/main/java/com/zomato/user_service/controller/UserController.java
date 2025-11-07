package com.zomato.user_service.controller;

import com.zomato.user_service.dto.login.LoginRequestDto;
import com.zomato.user_service.dto.signupCustomer.CustomerSignupRequestDto;
import com.zomato.user_service.dto.signupRestaurant.RestaurantSignupRequestDto;
import com.zomato.user_service.dto.signupRider.RiderSignupRequestDto;
import com.zomato.user_service.dto.updateLoggedInUser.UpdateUserRequestDto;
import com.zomato.user_service.enums.Status;
import com.zomato.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerSignupRequestDto customerSignupRequestDto)
    {
       return new ResponseEntity<>(userService.signupCustomer(customerSignupRequestDto), HttpStatus.CREATED);
    }
    @PostMapping("/signup/rider")
    public ResponseEntity<?> registerRider(@RequestBody RiderSignupRequestDto riderSignupRequestDto)
    {
        return new ResponseEntity<>(userService.signupRider(riderSignupRequestDto), HttpStatus.CREATED);
    }
    @PostMapping("/signup/restaurant")
    public ResponseEntity<?> registerRestaurant(@RequestBody RestaurantSignupRequestDto restaurantSignupRequestDto)
    {
        return new ResponseEntity<>(userService.signupRestaurant(restaurantSignupRequestDto), HttpStatus.CREATED);
    }
    @GetMapping("/fetch/me")
    public ResponseEntity<?> registerRestaurant()
    {
        return new ResponseEntity<>(userService.fetchLoggedInUserProfile(), HttpStatus.OK);
    }
    @PutMapping("/update/me")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequestDto updateUserRequestDto)
    {
        return new ResponseEntity<>(userService.updateLoggedInUser(updateUserRequestDto), HttpStatus.OK);
    }
    @PutMapping("/update/me/{password}")
    public ResponseEntity<?> updatePasswordForLoggedInUser(@PathVariable("password") String password)
    {
        return new ResponseEntity<>(userService.updatePasswordForLoggedInUser(password), HttpStatus.OK);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> changeStatusOfUserByUUID(@PathVariable("id") UUID id, @RequestBody Status status)
    {
        return new ResponseEntity<>(userService.changeStatusOfUserByUUID(id,status), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserByUUID(@PathVariable("id") UUID id)
    {
        return new ResponseEntity<>(userService.deleteUserByUUID(id), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> deleteUserByUUID(@RequestBody LoginRequestDto requestDto)
    {
        return new ResponseEntity<>(userService.login(requestDto), HttpStatus.OK);
    }

    //bridge method for restaurant-service
    @GetMapping("/get/restaurant-details")
    public ResponseEntity<?> getRestaurant()
    {
        return new ResponseEntity<>(userService.getRestaurantsList(), HttpStatus.OK);
    }

}
