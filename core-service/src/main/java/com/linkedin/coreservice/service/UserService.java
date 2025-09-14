package com.linkedin.coreservice.service;

import com.linkedin.coreservice.dto.UserRegistrationRequest;
import com.linkedin.coreservice.entity.User;
import com.linkedin.coreservice.exception.EmailAlreadyExistsException;
import com.linkedin.coreservice.exception.UserNotFoundException;
import com.linkedin.coreservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GraphSyncService graphSyncService;
    
    @Autowired
    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      GraphSyncService graphSyncService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.graphSyncService = graphSyncService;
    }
    
    public User createUser(UserRegistrationRequest request) {
        logger.info("Creating new user with email: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }
        
        // Create user entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setHeadline(request.getHeadline());
        user.setSummary(request.getSummary());
        user.setLocation(request.getLocation());
        user.setIndustry(request.getIndustry());
        user.setStatus(User.UserStatus.ACTIVE);
        
        // Save user
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        
        // Sync with graph service asynchronously
        try {
            graphSyncService.syncUser(savedUser);
        } catch (Exception e) {
            logger.warn("Failed to sync user to graph service: {}", e.getMessage());
        }
        
        return savedUser;
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findByIdOptional(Long id) {
        return userRepository.findById(id);
    }
    
    public User updateUser(Long id, UserRegistrationRequest request) {
        logger.info("Updating user with ID: {}", id);
        
        User user = findById(id);
        
        // Update fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getHeadline() != null) {
            user.setHeadline(request.getHeadline());
        }
        if (request.getSummary() != null) {
            user.setSummary(request.getSummary());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }
        if (request.getIndustry() != null) {
            user.setIndustry(request.getIndustry());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        
        // Sync with graph service asynchronously
        try {
            graphSyncService.syncUser(updatedUser);
        } catch (Exception e) {
            logger.warn("Failed to sync user update to graph service: {}", e.getMessage());
        }
        
        return updatedUser;
    }
    
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        
        User user = findById(id);
        user.setStatus(User.UserStatus.INACTIVE);
        userRepository.save(user);
        
        logger.info("User deleted successfully with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public Page<User> findAllActiveUsers(Pageable pageable) {
        return userRepository.findByStatus(User.UserStatus.ACTIVE, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String industry, String location, String search, Pageable pageable) {
        return userRepository.findByStatusAndFilters(
            User.UserStatus.ACTIVE, 
            industry, 
            location, 
            search, 
            pageable
        );
    }
    
    public boolean validatePassword(String plainPassword, String encodedPassword) {
        return passwordEncoder.matches(plainPassword, encodedPassword);
    }
    
    @Transactional(readOnly = true)
    public User authenticate(String email, String password) {
        User user = findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Invalid email or password"));
        
        if (!validatePassword(password, user.getPassword())) {
            throw new UserNotFoundException("Invalid email or password");
        }
        
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new UserNotFoundException("User account is not active");
        }
        
        return user;
    }
    
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countByStatus(User.UserStatus.ACTIVE);
    }
}
