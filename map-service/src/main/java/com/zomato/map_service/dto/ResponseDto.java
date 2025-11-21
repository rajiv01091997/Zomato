package com.zomato.map_service.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class ResponseDto {
    public String distance; // e.g., "10.2 km"
    public String duration; // e.g., "22 mins"
}
