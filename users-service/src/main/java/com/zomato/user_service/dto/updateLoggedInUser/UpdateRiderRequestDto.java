package com.zomato.user_service.dto.updateLoggedInUser;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateRiderRequestDto {
    @Size(max = 50, message = "Vehicle type can be at most 50 characters")
    private String vehicleType;
    @Size(max = 255, message = "Permanent address can be at most 255 characters")
    private String permanentAddress;
    @Size(max = 15, message = "License plate can be at most 15 characters")
    private String licensePlate;
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double currentLatitude;
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double currentLongitude;
    private Boolean activeStatus;
}
