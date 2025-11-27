package com.zomato.order_service.feign;

import com.zomato.order_service.dto.feign.fetch.map.RequestDto;
import com.zomato.order_service.dto.feign.fetch.map.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="map-service",url="localhost:8089/api/distance")
public interface MapServiceClient {

    @PostMapping("/calculate")
    public ResponseDto calculateDistance(@RequestBody RequestDto req);

}
