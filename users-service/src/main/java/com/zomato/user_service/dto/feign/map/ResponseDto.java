package com.zomato.user_service.dto.feign.map;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class ResponseDto {
    private String distance; // e.g., "10.2 km"
    private String duration; // e.g., "22 mins"
}
