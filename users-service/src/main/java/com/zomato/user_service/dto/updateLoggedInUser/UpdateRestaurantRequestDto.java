package com.zomato.user_service.dto.updateLoggedInUser;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateRestaurantRequestDto {

    @Size(max = 100, message = "Restaurant name can be at most 100 characters")
    private String restaurantName;

    @Size(max = 255, message = "Restaurant address can be at most 255 characters")
    private String restaurantAddress;

    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double longitude;

    @Size(max = 50, message = "Business license number can be at most 50 characters")
    private String businessLicenseNumber;

    @Pattern(
            regexp = "^([1-9]|1[0-2])\\s?(AM|PM)\\s?-\\s?([1-9]|1[0-2])\\s?(AM|PM)$",
            message = "Working hours must be in format '9 AM - 9 PM'"
    )
    private String workingHours;
}
