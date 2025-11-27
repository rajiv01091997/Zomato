package com.zomato.user_service.controller;

import com.zomato.user_service.dto.login.LoginRequestDto;
import com.zomato.user_service.dto.signupCustomer.CustomerSignupRequestDto;
import com.zomato.user_service.dto.signupRestaurant.RestaurantSignupRequestDto;
import com.zomato.user_service.dto.signupRider.RiderSignupRequestDto;
import com.zomato.user_service.dto.updateLoggedInUser.UpdateUserRequestDto;
import com.zomato.user_service.enums.Status;
import com.zomato.user_service.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup/customer")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerSignupRequestDto customerSignupRequestDto)
    {
       return new ResponseEntity<>(userService.signupCustomer(customerSignupRequestDto), HttpStatus.CREATED);
    }
    @PostMapping("/signup/rider")
    public ResponseEntity<?> registerRider(@Valid @RequestBody RiderSignupRequestDto riderSignupRequestDto)
    {
        return new ResponseEntity<>(userService.signupRider(riderSignupRequestDto), HttpStatus.CREATED);
    }
    @PostMapping("/signup/restaurant")
    public ResponseEntity<?> registerRestaurant(@Valid @RequestBody RestaurantSignupRequestDto restaurantSignupRequestDto)
    {
        return new ResponseEntity<>(userService.signupRestaurant(restaurantSignupRequestDto), HttpStatus.CREATED);
    }
    @GetMapping("/fetch/me")
    public ResponseEntity<?> registerRestaurant()
    {
        return new ResponseEntity<>(userService.fetchLoggedInUserProfile(), HttpStatus.OK);
    }
    @PutMapping("/update/me")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequestDto updateUserRequestDto)
    {
        return new ResponseEntity<>(userService.updateLoggedInUser(updateUserRequestDto), HttpStatus.OK);
    }
    @PutMapping("/update/me/{password}")
    public ResponseEntity<?> updatePasswordForLoggedInUser(
            @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character"
                     )
            @PathVariable("password") String password)
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



}
