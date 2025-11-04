package com.zomato.user_service.dto.fetchLoggedInUserProfile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zomato.user_service.enums.Role;
import com.zomato.user_service.enums.Status;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/* @JsonInclude(JsonInclude.Include.NON_NULL):
for excluding the null fields because
when user Role is customer then Rider and Restaurant will be null,
similarly for other roles also this follows,
also logged in customer/Rider/Restaurant want only their profile related details,
which we handle in service using if and attach details based on their Role
hence other 2 remains null and we exclude it */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponseDto {
    private UUID id;
    private String userName;
    private String email;
    private String phoneNumber;
    private Role role;
    private Status status;
    private LocalDateTime createdAt;
    @JsonInclude(JsonInclude.Include.ALWAYS)//overrides classlevel jsonInclude
    private LocalDateTime updatedAt;

    private List<CustomerProfileResponseDto> customerProfileResponseDtoList;
    private RiderProfileResponseDto riderProfileResponseDto;
    private RestaurantProfileResponseDto restaurantProfileResponseDto;
}
