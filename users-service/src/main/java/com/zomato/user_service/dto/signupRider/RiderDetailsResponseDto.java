package com.zomato.user_service.dto.signupRider;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RiderDetailsResponseDto {
    private UUID id;
    private String vehicleType;
    private String permanentAddress;
    private String licensePlate;
    private Double currentLatitude;
    private Double currentLongitude;
    private Boolean activeStatus;
}
