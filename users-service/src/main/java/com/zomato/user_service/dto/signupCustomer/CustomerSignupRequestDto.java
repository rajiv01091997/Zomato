package com.zomato.user_service.dto.signupCustomer;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerSignupRequestDto {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;

    private CustomerAddressRequestDto primaryAddress;
}
