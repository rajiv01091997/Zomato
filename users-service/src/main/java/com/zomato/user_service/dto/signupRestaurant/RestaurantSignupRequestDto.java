package com.zomato.user_service.dto.signupRestaurant;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RestaurantSignupRequestDto {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
    private RestaurantDetailsRequestDto restaurantDetailsRequestDto;
}
