package com.zomato.rider_service.entity;

import com.zomato.rider_service.enums.Vehicles;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name="riders")
public class Rider {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private Vehicles vehicleType;
    private String vehicleNumber;
    private String licenseNumber;
    private boolean isAvailable;
    private String currentLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "rider",cascade= CascadeType.ALL,orphanRemoval = true)
    private RiderBankDetail bankDetail;
}
