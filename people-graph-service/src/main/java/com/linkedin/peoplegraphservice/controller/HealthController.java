package com.linkedin.peoplegraphservice.controller;

import com.linkedin.peoplegraphservice.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("message", "People graph service is healthy");
        healthData.put("timestamp", LocalDateTime.now());
        healthData.put("service", "people-graph-service");
        healthData.put("status", "UP");
        
        return ResponseEntity.ok(ApiResponse.success("Service is healthy", healthData));
    }
}
