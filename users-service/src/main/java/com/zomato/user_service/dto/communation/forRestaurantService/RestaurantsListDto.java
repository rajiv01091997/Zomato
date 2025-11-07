package com.zomato.user_service.dto.communation.forRestaurantService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantsListDto {
    private UUID restaurantId;
    private String restaurantName;
    private String email;
    private String phoneNumber;
    private String restaurantAddress;
    private Double longitude;
    private Double latitude;
    private String businessLicenseNumber;
    private String workingHours;
}
