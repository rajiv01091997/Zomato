package com.zomato.map_service.controller;

import com.zomato.map_service.dto.RequestDto;
import com.zomato.map_service.dto.ResponseDto;
import com.zomato.map_service.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/distance")
public class DistanceController {
         @Autowired
        private MapService distanceMatrixService;



        @PostMapping("/calculate")
        public ResponseEntity<?> calculateDistance(@RequestBody RequestDto req) {
            try {
                ResponseDto result = distanceMatrixService.getDistanceAndDuration(req);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                // Handle errors gracefully
                return ResponseEntity.status(500).body(new ResponseDto("Error", "Error"));
            }
        }
}
