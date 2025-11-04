package com.zomato.user_service.dto.signupRider;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RiderDetailsRequestDto {
    private String vehicleType;
    private String licensePlate;
    private Double currentLatitude;
    private Double currentLongitude;
    private Boolean activeStatus;
}
