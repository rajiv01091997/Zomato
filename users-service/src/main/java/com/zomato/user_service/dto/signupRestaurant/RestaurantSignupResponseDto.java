package com.zomato.user_service.dto.signupRestaurant;

import com.zomato.user_service.enums.Role;
import com.zomato.user_service.enums.Status;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RestaurantSignupResponseDto {
    private UUID id;
    private String userName;
    private String email;
    private String phoneNumber;
    private Role role;
    private Status status;

    private RestaurantDetailsResponseDto restaurantDetailsResponseDto;
}
