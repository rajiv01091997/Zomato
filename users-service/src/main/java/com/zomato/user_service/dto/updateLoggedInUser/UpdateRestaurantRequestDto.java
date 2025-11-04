package com.zomato.user_service.dto.updateLoggedInUser;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateRestaurantRequestDto {
    private String restaurantName;
    private String restaurantAddress;
    private Double latitude;
    private Double longitude;
    private String businessLicenseNumber;
    private String workingHours;
}
