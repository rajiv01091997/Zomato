package com.zomato.user_service.dto.updateLoggedInUser;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateRiderResponseDto {
    private UUID id;
    private String vehicleType;
    private String permanentAddress;
    private String licensePlate;
    private Double currentLatitude;
    private Double currentLongitude;
    private Boolean activeStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
