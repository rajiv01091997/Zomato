package com.zomato.user_service.dto.updateLoggedInUser;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateCustomerRequestDto {
    private UUID id;
    @Size(max = 255, message = "Address line can be at most 255 characters")
    private String addressLine;
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double latitude;
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double longitude;
    @NotNull(message = "isDefault field is required")
    private Boolean isDefault;
}
