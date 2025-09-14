package com.linkedin.coreservice.service;

import com.linkedin.coreservice.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

@Service
public class GraphSyncService {
    
    private static final Logger logger = LoggerFactory.getLogger(GraphSyncService.class);
    
    private final RestTemplate restTemplate;
    private final String graphServiceUrl;
    
    public GraphSyncService(@Value("${graph.service.url}") String graphServiceUrl, 
                           RestTemplate restTemplate) {
        this.graphServiceUrl = graphServiceUrl;
        this.restTemplate = restTemplate;
    }
    
    @Async
    public void syncUser(User user) {
        try {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("email", user.getEmail());
            userData.put("firstName", user.getFirstName());  // Changed from first_name to firstName
            userData.put("lastName", user.getLastName());    // Changed from last_name to lastName
            userData.put("headline", user.getHeadline());
            userData.put("location", user.getLocation());
            userData.put("industry", user.getIndustry());
            
            // Create HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create HTTP entity with headers and body
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(userData, headers);
            
            // Make synchronous REST call
            String url = graphServiceUrl + "/api/graph/sync/user";
            logger.debug("Attempting to sync user to graph service. URL: {}, Data: {}", url, userData);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("User synced successfully to graph service: {}", user.getId());
            } else {
                logger.error("Failed to sync user to graph service. Status: {}, Response: {}", 
                    response.getStatusCode(), response.getBody());
            }
            
        } catch (RestClientException e) {
            logger.error("Failed to sync user to graph service: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error syncing user to graph service: {}", e.getMessage(), e);
        }
    }
    
    @Async
    public void syncCompany(Map<String, Object> companyData) {
        try {
            // Create HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create HTTP entity with headers and body
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(companyData, headers);
            
            // Make synchronous REST call
            String url = graphServiceUrl + "/api/graph/sync/company";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Company synced successfully to graph service");
            } else {
                logger.error("Failed to sync company to graph service. Status: {}", response.getStatusCode());
            }
            
        } catch (RestClientException e) {
            logger.error("Failed to sync company to graph service: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error syncing company to graph service: {}", e.getMessage(), e);
        }
    }
}
