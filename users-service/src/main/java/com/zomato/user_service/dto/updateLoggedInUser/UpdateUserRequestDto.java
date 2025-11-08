package com.zomato.user_service.dto.updateLoggedInUser;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateUserRequestDto {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;

    private List<UpdateCustomerRequestDto> updateCustomerRequestDtoList;
    private UpdateRiderRequestDto updateRiderRequestDto;
    private UpdateRestaurantRequestDto updateRestaurantRequestDto;
}
