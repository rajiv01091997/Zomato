package com.zomato.user_service.dto.signupRestaurant;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RestaurantDetailsResponseDto {

        private UUID id;
        private String restaurantName;
        private String restaurantAddress;
        private Double latitude;
        private Double longitude;
        private String businessLicenseNumber;
        private String workingHours; // Example: "9 AM - 9 PM"


}
