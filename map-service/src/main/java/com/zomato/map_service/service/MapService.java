package com.zomato.map_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zomato.map_service.dto.RequestDto;
import com.zomato.map_service.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



@Service
public class MapService {
    @Value("${google.maps.api.key}")
    private String apiKey;
    public ResponseDto getDistanceAndDuration(RequestDto req) throws Exception {
        String origin = req.originLat + "," + req.originLon;
        String destination = req.destLat + "," + req.destLon;
        // Use Directions API (not Distance Matrix)
        String url = "https://maps.googleapis.com/maps/api/directions/json"
                + "?origin=" + origin
                + "&destination=" + destination
                + "&mode=driving"
                + "&departure_time=now"
                + "&key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        JsonNode leg = root.path("routes").get(0).path("legs").get(0);

        String distance = leg.path("distance").path("text").asText();
        String duration = leg.path("duration_in_traffic").path("text").asText();
        // Fallback if duration_in_traffic is missing
        if (duration == null || duration.isEmpty()) {
            duration = leg.path("duration").path("text").asText();
        }

        return new ResponseDto(distance, duration);
    }



}
