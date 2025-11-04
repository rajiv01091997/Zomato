package com.zomato.user_service.dto.updateLoggedInUser;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserResponseDto {
    private UUID id;
    private String userName;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<UpdateCustomerResponseDto> updateCustomerResponseDtoList;
    private UpdateRiderResponseDto updateRiderResponseDto;
    private UpdateRestaurantResponseDto updateRestaurantResponseDto;
}
