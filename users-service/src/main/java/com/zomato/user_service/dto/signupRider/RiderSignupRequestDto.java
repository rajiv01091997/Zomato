package com.zomato.user_service.dto.signupRider;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RiderSignupRequestDto {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;

    private RiderDetailsRequestDto riderDetailsRequestDto;
}
