package com.linkedin.coreservice.controller;

import com.linkedin.coreservice.dto.ApiResponse;
import com.linkedin.coreservice.dto.UserRegistrationRequest;
import com.linkedin.coreservice.entity.User;
import com.linkedin.coreservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://localhost:8081"},
             allowCredentials = "true")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String search) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
                
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<User> userPage;
            if (industry != null || location != null || search != null) {
                userPage = userService.searchUsers(industry, location, search, pageable);
            } else {
                userPage = userService.findAllActiveUsers(pageable);
            }
            
            List<Map<String, Object>> users = userPage.getContent().stream()
                    .map(this::sanitizeUser)
                    .collect(Collectors.toList());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("users", users);
            responseData.put("pagination", Map.of(
                "page", userPage.getNumber(),
                "size", userPage.getSize(),
                "total", userPage.getTotalElements(),
                "totalPages", userPage.getTotalPages()
            ));
            
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", responseData));
            
        } catch (Exception e) {
            logger.error("Failed to retrieve users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", sanitizeUser(user));
            
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", responseData));
            
        } catch (Exception e) {
            logger.error("Failed to retrieve user with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("authentication.name == @userService.findById(#id).email")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRegistrationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            User updatedUser = userService.updateUser(id, request);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", sanitizeUser(updatedUser));
            
            logger.info("User updated successfully with ID: {}", id);
            
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", responseData));
            
        } catch (Exception e) {
            logger.error("Failed to update user with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("authentication.name == @userService.findById(#id).email")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            
            logger.info("User deleted successfully with ID: {}", id);
            
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to delete user with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchUsers(
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, 
                Sort.by(Sort.Direction.DESC, "createdAt"));
            
            Page<User> userPage = userService.searchUsers(industry, location, search, pageable);
            
            List<Map<String, Object>> users = userPage.getContent().stream()
                    .map(this::sanitizeUser)
                    .collect(Collectors.toList());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("users", users);
            responseData.put("pagination", Map.of(
                "page", userPage.getNumber(),
                "size", userPage.getSize(),
                "total", userPage.getTotalElements(),
                "totalPages", userPage.getTotalPages()
            ));
            
            return ResponseEntity.ok(ApiResponse.success("Search completed successfully", responseData));
            
        } catch (Exception e) {
            logger.error("Failed to search users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        try {
            long totalUsers = userService.getActiveUserCount();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalActiveUsers", totalUsers);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("stats", stats);
            
            return ResponseEntity.ok(ApiResponse.success("User statistics retrieved successfully", responseData));
            
        } catch (Exception e) {
            logger.error("Failed to retrieve user statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
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
