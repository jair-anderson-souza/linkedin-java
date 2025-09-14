package com.linkedin.coreservice.controller;

import com.linkedin.coreservice.dto.ApiResponse;
import com.linkedin.coreservice.dto.LoginRequest;
import com.linkedin.coreservice.dto.UserRegistrationRequest;
import com.linkedin.coreservice.entity.User;
import com.linkedin.coreservice.service.UserService;
import com.linkedin.coreservice.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://localhost:8081"},
             allowCredentials = "true")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            @Valid @RequestBody UserRegistrationRequest request) {
        
        logger.info("Registration request for email: {}", request.getEmail());
        
        try {
            User user = userService.createUser(request);
            String token = jwtUtil.generateToken(user.getEmail(), user.getId());
            
            // Create response data
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", sanitizeUser(user));
            responseData.put("token", token);
            
            logger.info("User registered successfully: {}", request.getEmail());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", responseData));
                    
        } catch (Exception e) {
            logger.error("Registration failed for email {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @Valid @RequestBody LoginRequest request) {
        
        logger.info("Login request for email: {}", request.getEmail());
        
        try {
            User user = userService.authenticate(request.getEmail(), request.getPassword());
            String token = jwtUtil.generateToken(user.getEmail(), user.getId());
            
            // Create response data
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", sanitizeUser(user));
            responseData.put("token", token);
            
            logger.info("User logged in successfully: {}", request.getEmail());
            
            return ResponseEntity.ok(
                    ApiResponse.success("Login successful", responseData));
                    
        } catch (Exception e) {
            logger.error("Login failed for email {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid email or password"));
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", sanitizeUser(user));
            
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", responseData));
            
        } catch (Exception e) {
            logger.error("Failed to get profile for user {}: {}", userDetails.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<Map<String, String>>> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractUsername(token);
                Long userId = jwtUtil.extractUserId(token);
                
                Map<String, String> responseData = new HashMap<>();
                responseData.put("email", email);
                responseData.put("userId", userId.toString());
                
                return ResponseEntity.ok(
                        ApiResponse.success("Token is valid", responseData));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid token format"));
        }
    }
    
    // Helper method to remove sensitive data from user object
    private Map<String, Object> sanitizeUser(User user) {
        Map<String, Object> sanitizedUser = new HashMap<>();
        sanitizedUser.put("id", user.getId());
        sanitizedUser.put("email", user.getEmail());
        sanitizedUser.put("firstName", user.getFirstName());
        sanitizedUser.put("lastName", user.getLastName());
        sanitizedUser.put("headline", user.getHeadline());
        sanitizedUser.put("summary", user.getSummary());
        sanitizedUser.put("profileImageUrl", user.getProfileImageUrl());
        sanitizedUser.put("location", user.getLocation());
        sanitizedUser.put("industry", user.getIndustry());
        sanitizedUser.put("status", user.getStatus());
        sanitizedUser.put("createdAt", user.getCreatedAt());
        // Note: password is intentionally excluded
        return sanitizedUser;
    }
}
