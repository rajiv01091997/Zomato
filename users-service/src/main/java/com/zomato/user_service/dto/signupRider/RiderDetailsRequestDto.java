package com.zomato.user_service.dto.signupRider;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RiderDetailsRequestDto {
    @NotBlank(message = "Vehicle type is required")
    @Size(max = 50, message = "Vehicle type can be at most 50 characters")
    private String vehicleType;
    @NotBlank(message = "Permanent address is required")
    @Size(max = 255, message = "Permanent address can be at most 255 characters")
    private String permanentAddress;
    @NotBlank(message = "License plate is required")
    @Size(max = 15, message = "License plate can be at most 15 characters")
    private String licensePlate;
    @NotNull(message = "Current latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double currentLatitude;
    @NotNull(message = "Current longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double currentLongitude;
    @NotNull(message = "Active status is required")
    private Boolean activeStatus;
    @NotNull(message = "Availability status is required")
    private Boolean isAvailable;
}
