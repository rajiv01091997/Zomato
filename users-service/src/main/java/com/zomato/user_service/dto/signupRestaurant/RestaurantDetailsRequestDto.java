package com.zomato.user_service.dto.signupRestaurant;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RestaurantDetailsRequestDto {
    @NotBlank(message = "Restaurant name is required")
    @Size(max = 100, message = "Restaurant name can be at most 100 characters")
    private String restaurantName;
    @NotBlank(message = "Restaurant address is required")
    @Size(max = 255, message = "Restaurant address can be at most 255 characters")
    private String restaurantAddress;
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double latitude;
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double longitude;
    @NotBlank(message = "Business license number is required")
    @Size(max = 50, message = "Business license number can be at most 50 characters")
    private String businessLicenseNumber;
    @NotBlank(message = "Working hours are required")
    @Pattern(
            regexp = "^([1-9]|1[0-2])\\s?(AM|PM)\\s?-\\s?([1-9]|1[0-2])\\s?(AM|PM)$",
            message = "Working hours must be in format '9 AM - 9 PM'"
    )
    private String workingHours; // Example: "9 AM - 9 PM"


}
