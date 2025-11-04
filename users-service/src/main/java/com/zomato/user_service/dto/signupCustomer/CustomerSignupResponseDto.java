package com.zomato.user_service.dto.signupCustomer;

import com.zomato.user_service.enums.Role;
import com.zomato.user_service.enums.Status;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerSignupResponseDto {
    private UUID id;
    private String userName;
    private String email;
    private String phoneNumber;
    private Role role;
    private Status status;
    private CustomerAddressResponseDto address;
}
