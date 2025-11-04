package com.zomato.user_service.dto.signupCustomer;

import lombok.*;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerAddressResponseDto {
        private UUID id;

        private String addressLine;

        private Double latitude;

        private Double longitude;

        private Boolean isDefault;

}
