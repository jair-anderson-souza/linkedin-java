package com.linkedin.peoplegraphservice.service;

import com.linkedin.peoplegraphservice.dto.ConnectionSuggestion;
import com.linkedin.peoplegraphservice.dto.UserSyncRequest;
import com.linkedin.peoplegraphservice.entity.CompanyNode;
import com.linkedin.peoplegraphservice.entity.SkillNode;
import com.linkedin.peoplegraphservice.entity.UserNode;
import com.linkedin.peoplegraphservice.exception.UserNotFoundException;
import com.linkedin.peoplegraphservice.repository.CompanyNodeRepository;
import com.linkedin.peoplegraphservice.repository.SkillNodeRepository;
import com.linkedin.peoplegraphservice.repository.UserNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GraphService {
    
    private static final Logger logger = LoggerFactory.getLogger(GraphService.class);
    
    private final UserNodeRepository userRepository;
    private final CompanyNodeRepository companyRepository;
    private final SkillNodeRepository skillRepository;
    
    @Autowired
    public GraphService(UserNodeRepository userRepository,
                       CompanyNodeRepository companyRepository,
                       SkillNodeRepository skillRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;
    }
    
    // User operations
    public UserNode createOrUpdateUser(UserSyncRequest request) {
        logger.info("Creating/updating user with ID: {}", request.getId());
        
        UserNode user = userRepository.findById(request.getId())
                .orElse(new UserNode());
        
        user.setId(request.getId());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setHeadline(request.getHeadline());
        user.setLocation(request.getLocation());
        user.setIndustry(request.getIndustry());
        
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        
        UserNode savedUser = userRepository.save(user);
        logger.info("User created/updated successfully: {}", savedUser.getId());
        
        return savedUser;
    }
    
    public CompanyNode createOrUpdateCompany(Long id, String name, String description, 
                                           String industry, String location) {
        logger.info("Creating/updating company with ID: {}", id);
        
        CompanyNode company = companyRepository.findById(id)
                .orElse(new CompanyNode());
        
        company.setId(id);
        company.setName(name);
        company.setDescription(description);
        company.setIndustry(industry);
        company.setLocation(location);
        
        if (company.getCreatedAt() == null) {
            company.setCreatedAt(LocalDateTime.now());
        }
        
        CompanyNode savedCompany = companyRepository.save(company);
        logger.info("Company created/updated successfully: {}", savedCompany.getId());
        
        return savedCompany;
    }
    
    public SkillNode createOrUpdateSkill(String skillName) {
        logger.info("Creating/updating skill: {}", skillName);
        
        SkillNode skill = skillRepository.findById(skillName)
                .orElse(new SkillNode(skillName));
        
        SkillNode savedSkill = skillRepository.save(skill);
        logger.info("Skill created/updated successfully: {}", savedSkill.getName());
        
        return savedSkill;
    }
    
    // Connection operations
    public void connectUsers(Long fromUserId, Long toUserId) {
        logger.info("Connecting users {} and {}", fromUserId, toUserId);
        
        if (!userRepository.existsById(fromUserId) || !userRepository.existsById(toUserId)) {
            throw new UserNotFoundException("One or both users not found");
        }
        
        userRepository.connectUsers(fromUserId, toUserId);
        logger.info("Users connected successfully: {} <-> {}", fromUserId, toUserId);
    }
    
    public void followCompany(Long userId, Long companyId) {
        logger.info("User {} following company {}", userId, companyId);
        
        if (!userRepository.existsById(userId) || !companyRepository.existsById(companyId)) {
            throw new UserNotFoundException("User or company not found");
        }
        
        userRepository.followCompany(userId, companyId);
        logger.info("User {} now follows company {}", userId, companyId);
    }
    
    public void addWorkExperience(Long userId, Long companyId, String position, 
                                String startDate, String endDate) {
        logger.info("Adding work experience for user {} at company {}", userId, companyId);
        
        if (!userRepository.existsById(userId) || !companyRepository.existsById(companyId)) {
            throw new UserNotFoundException("User or company not found");
        }
        
        userRepository.addWorkExperience(userId, companyId, position, startDate, endDate);
        logger.info("Work experience added for user {} at company {}", userId, companyId);
    }
    
    // Query operations
    @Transactional(readOnly = true)
    public List<ConnectionSuggestion> getConnectionSuggestions(Long userId, Integer limit) {
        logger.info("Getting connection suggestions for user: {}", userId);
        
        List<UserNode> suggestions = userRepository.findConnectionSuggestions(userId, limit);
        return suggestions.stream()
                .map(this::mapToConnectionSuggestion)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ConnectionSuggestion> getPeopleYouMayKnow(Long userId, Integer limit) {
        logger.info("Getting people you may know for user: {}", userId);
        
        List<UserNode> suggestions = userRepository.findPeopleYouMayKnow(userId, limit);
        return suggestions.stream()
                .map(this::mapToConnectionSuggestion)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Integer getConnectionCount(Long userId) {
        logger.info("Getting connection count for user: {}", userId);
        
        Integer count = userRepository.getConnectionCount(userId);
        return count != null ? count : 0;
    }
    
    @Transactional(readOnly = true)
    public List<ConnectionSuggestion> getMutualConnections(Long userId1, Long userId2) {
        logger.info("Getting mutual connections between users {} and {}", userId1, userId2);
        
        List<UserNode> mutualConnections = userRepository.findMutualConnections(userId1, userId2);
        return mutualConnections.stream()
                .map(this::mapToConnectionSuggestion)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ConnectionSuggestion> getAffinityRanking(Long userId, Integer limit) {
        logger.info("Getting affinity ranking for user: {}", userId);
        
        List<UserNode> ranking = userRepository.findAffinityRanking(userId, limit);
        return ranking.stream()
                .map(this::mapToConnectionSuggestion)
                .collect(Collectors.toList());
    }
    
    // Helper methods
    private ConnectionSuggestion mapToConnectionSuggestion(UserNode user) {
        ConnectionSuggestion suggestion = new ConnectionSuggestion();
        suggestion.setId(user.getId());
        suggestion.setFirstName(user.getFirstName());
        suggestion.setLastName(user.getLastName());
        suggestion.setHeadline(user.getHeadline());
        suggestion.setIndustry(user.getIndustry());
        suggestion.setLocation(user.getLocation());
        return suggestion;
    }
}
