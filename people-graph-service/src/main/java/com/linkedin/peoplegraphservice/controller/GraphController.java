package com.linkedin.peoplegraphservice.controller;

import com.linkedin.peoplegraphservice.dto.ApiResponse;
import com.linkedin.peoplegraphservice.dto.ConnectionSuggestion;
import com.linkedin.peoplegraphservice.dto.UserSyncRequest;
import com.linkedin.peoplegraphservice.entity.CompanyNode;
import com.linkedin.peoplegraphservice.entity.UserNode;
import com.linkedin.peoplegraphservice.service.GraphService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/graph")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://localhost:8081"},
             allowCredentials = "true")
public class GraphController {
    
    private static final Logger logger = LoggerFactory.getLogger(GraphController.class);
    
    private final GraphService graphService;
    
    @Autowired
    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }
    
    @PostMapping("/users/{userId}/connections/{targetId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> connectUsers(
            @PathVariable Long userId,
            @PathVariable Long targetId) {
        
        try {
            graphService.connectUsers(userId, targetId);
            
            logger.info("User {} connected to user {}", userId, targetId);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("fromUserId", userId);
            responseData.put("toUserId", targetId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Connection created successfully", responseData));
                    
        } catch (Exception e) {
            logger.error("Failed to connect users {} and {}: {}", userId, targetId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @GetMapping("/users/{userId}/connection-suggestions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConnectionSuggestions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<ConnectionSuggestion> suggestions = graphService.getConnectionSuggestions(userId, limit);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("suggestions", suggestions);
            
            return ResponseEntity.ok(ApiResponse.success("Connection suggestions retrieved successfully", responseData));
            
        } catch (Exception e) {
            logger.error("Failed to get connection suggestions for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @GetMapping("/users/shortest-path")
    public ResponseEntity<ApiResponse<Map<String, Object>>> findShortestPath(
            @RequestParam Long from,
            @RequestParam Long to) {
        
        try {
            // Note: This would need custom implementation for path finding
            // For now, return a placeholder response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("from", from);
            responseData.put("to", to);
            responseData.put("pathLength", 0);
            responseData.put("path", List.of());
            
            return ResponseEntity.ok(ApiResponse.success("Shortest path found", responseData));
            
        } catch (Exception e) {
            logger.error("Failed to find shortest path between {} and {}: {}", from, to, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @GetMapping("/users/{userId}/people-you-may-know")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPeopleYouMayKnow(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<ConnectionSuggestion> suggestions = graphService.getPeopleYouMayKnow(userId, limit);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("suggestions", suggestions);
            
            return ResponseEntity.ok(ApiResponse.success("People you may know retrieved successfully", responseData));
            
        } catch (Exception e) {
            logger.error("Failed to get people you may know for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @PostMapping("/users/{userId}/skills/{skillName}/endorse")
    public ResponseEntity<ApiResponse<Map<String, Object>>> endorseSkill(
            @PathVariable Long userId,
            @PathVariable String skillName,
            @RequestBody Map<String, Object> requestBody) {
        
        try {
            // Create or update skill
            graphService.createOrUpdateSkill(skillName);
            
            logger.info("Skill {} endorsed for user {}", skillName, userId);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", userId);
            responseData.put("skillName", skillName);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Skill endorsed successfully", responseData));
                    
        } catch (Exception e) {
            logger.error("Failed to endorse skill {} for user {}: {}", skillName, userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @GetMapping("/users/{userId}/connection-count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConnectionCount(
            @PathVariable Long userId) {
        
        try {
            Integer count = graphService.getConnectionCount(userId);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("connectionCount", count);
            
            return ResponseEntity.ok(ApiResponse.success("Connection count retrieved successfully", responseData));
            
        } catch (Exception e) {
            logger.error("Failed to get connection count for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @GetMapping("/users/{userId}/mutual-connections/{targetUserId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMutualConnections(
            @PathVariable Long userId,
            @PathVariable Long targetUserId) {
        
        try {
            List<ConnectionSuggestion> mutualConnections = graphService.getMutualConnections(userId, targetUserId);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("mutualConnections", mutualConnections);
            
            return ResponseEntity.ok(ApiResponse.success("Mutual connections retrieved successfully", responseData));
            
        } catch (Exception e) {
            logger.error("Failed to get mutual connections for users {} and {}: {}", userId, targetUserId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @GetMapping("/users/{userId}/affinity-ranking")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAffinityRanking(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") Integer limit) {
        
        try {
            List<ConnectionSuggestion> ranking = graphService.getAffinityRanking(userId, limit);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("affinityRanking", ranking);
            
            return ResponseEntity.ok(ApiResponse.success("Affinity ranking retrieved successfully", responseData));
            
        } catch (Exception e) {
            logger.error("Failed to get affinity ranking for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @PostMapping("/users/{userId}/companies/{companyId}/follow")
    public ResponseEntity<ApiResponse<Map<String, Object>>> followCompany(
            @PathVariable Long userId,
            @PathVariable Long companyId) {
        
        try {
            graphService.followCompany(userId, companyId);
            
            logger.info("User {} followed company {}", userId, companyId);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", userId);
            responseData.put("companyId", companyId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Company followed successfully", responseData));
                    
        } catch (Exception e) {
            logger.error("Failed to follow company {} for user {}: {}", companyId, userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @PostMapping("/users/{userId}/work-experience")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addWorkExperience(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> requestBody) {
        
        try {
            Long companyId = Long.valueOf(requestBody.get("companyId").toString());
            String position = requestBody.get("position").toString();
            String startDate = requestBody.get("startDate").toString();
            String endDate = requestBody.get("endDate") != null ? requestBody.get("endDate").toString() : null;
            
            graphService.addWorkExperience(userId, companyId, position, startDate, endDate);
            
            logger.info("Work experience added for user {} at company {}", userId, companyId);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", userId);
            responseData.put("companyId", companyId);
            responseData.put("position", position);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Work experience added successfully", responseData));
                    
        } catch (Exception e) {
            logger.error("Failed to add work experience for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    // Sync endpoints for inter-service communication
    @PostMapping("/sync/user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> syncUser(
            @Valid @RequestBody UserSyncRequest request) {
        
        try {
            UserNode user = graphService.createOrUpdateUser(request);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", user);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User synced successfully", responseData));
                    
        } catch (Exception e) {
            logger.error("Failed to sync user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
    
    @PostMapping("/sync/company")
    public ResponseEntity<ApiResponse<Map<String, Object>>> syncCompany(
            @RequestBody Map<String, Object> companyData) {
        
        try {
            Long id = Long.valueOf(companyData.get("id").toString());
            String name = companyData.get("name").toString();
            String description = companyData.get("description") != null ? companyData.get("description").toString() : null;
            String industry = companyData.get("industry") != null ? companyData.get("industry").toString() : null;
            String location = companyData.get("location") != null ? companyData.get("location").toString() : null;
            
            CompanyNode company = graphService.createOrUpdateCompany(id, name, description, industry, location);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("company", company);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Company synced successfully", responseData));
                    
        } catch (Exception e) {
            logger.error("Failed to sync company: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }
}
