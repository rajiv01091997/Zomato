package com.zomato.user_service.dto.updateLoggedInUser;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateRiderRequestDto {
    private String vehicleType;
    private String licensePlate;
    private Double currentLatitude;
    private Double currentLongitude;
    private Boolean activeStatus;
}
