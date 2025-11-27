package com.zomato.order_service.dto.feign.fetch.map;

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
