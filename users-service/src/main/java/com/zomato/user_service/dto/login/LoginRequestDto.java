package com.zomato.user_service.dto.login;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequestDto {
    private String userName;
    private String password;
}
