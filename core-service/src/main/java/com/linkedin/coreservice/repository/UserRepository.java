package com.linkedin.coreservice.repository;

import com.linkedin.coreservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Page<User> findByStatus(User.UserStatus status, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.status = :status AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.headline) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findByStatusAndSearch(@Param("status") User.UserStatus status, 
                                    @Param("search") String search, 
                                    Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.status = :status AND u.industry = :industry")
    Page<User> findByStatusAndIndustry(@Param("status") User.UserStatus status, 
                                      @Param("industry") String industry, 
                                      Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.status = :status AND LOWER(u.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    Page<User> findByStatusAndLocation(@Param("status") User.UserStatus status, 
                                      @Param("location") String location, 
                                      Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.status = :status AND " +
           "(:industry IS NULL OR u.industry = :industry) AND " +
           "(:location IS NULL OR LOWER(u.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:search IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.headline) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findByStatusAndFilters(@Param("status") User.UserStatus status,
                                     @Param("industry") String industry,
                                     @Param("location") String location,
                                     @Param("search") String search,
                                     Pageable pageable);
    
    long countByStatus(User.UserStatus status);
}
