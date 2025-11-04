package com.zomato.user_service.service;

import com.zomato.user_service.dto.login.LoginRequestDto;
import com.zomato.user_service.dto.login.LoginResponseDto;
import com.zomato.user_service.dto.signupCustomer.CustomerSignupRequestDto;
import com.zomato.user_service.dto.signupCustomer.CustomerSignupResponseDto;
import com.zomato.user_service.dto.fetchLoggedInUserProfile.UserProfileResponseDto;
import com.zomato.user_service.dto.signupRestaurant.RestaurantSignupRequestDto;
import com.zomato.user_service.dto.signupRestaurant.RestaurantSignupResponseDto;
import com.zomato.user_service.dto.signupRider.RiderSignupRequestDto;
import com.zomato.user_service.dto.signupRider.RiderSignupResponseDto;
import com.zomato.user_service.dto.updateLoggedInUser.UpdateUserRequestDto;
import com.zomato.user_service.dto.updateLoggedInUser.UpdateUserResponseDto;
import com.zomato.user_service.enums.Status;

import java.util.UUID;

public interface UserServiceInterface {
    CustomerSignupResponseDto signupCustomer(CustomerSignupRequestDto customerSignupRequestDto);

    RiderSignupResponseDto signupRider(RiderSignupRequestDto riderSignupRequestDto);

    RestaurantSignupResponseDto signupRestaurant(RestaurantSignupRequestDto restaurantSignupRequestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    UserProfileResponseDto fetchLoggedInUserProfile();

    UpdateUserResponseDto updateLoggedInUser(UpdateUserRequestDto updateUserRequestDto);

    String updatePasswordForLoggedInUser(String password);
    //only for admin methods
    String changeStatusOfUserByUUID(UUID id, Status status);

    String deleteUserByUUID(UUID id);
}
