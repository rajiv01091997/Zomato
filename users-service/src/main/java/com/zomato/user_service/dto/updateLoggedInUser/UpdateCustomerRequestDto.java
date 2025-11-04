package com.zomato.user_service.dto.updateLoggedInUser;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateCustomerRequestDto {
    private String addressLine;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;
}
