package com.zomato.user_service.dto.login;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponseDto {

    private UUID id;
    private String token;

}
