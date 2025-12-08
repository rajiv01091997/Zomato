package com.zomato.user_service.dto.feign.map;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class RequestDto {
    private double originLat;
    private double originLon;
    private double destLat;
    private double destLon;
}
