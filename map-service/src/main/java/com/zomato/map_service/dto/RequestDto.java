package com.zomato.map_service.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class RequestDto {
    public double originLat;
    public double originLon;
    public double destLat;
    public double destLon;
}
