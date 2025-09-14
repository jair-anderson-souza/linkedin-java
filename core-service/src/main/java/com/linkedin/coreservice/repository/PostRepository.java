package com.linkedin.coreservice.repository;

import com.linkedin.coreservice.entity.Post;
import com.linkedin.coreservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    Page<Post> findByStatusOrderByCreatedAtDesc(Post.PostStatus status, Pageable pageable);
    
    Page<Post> findByUserAndStatusOrderByCreatedAtDesc(User user, Post.PostStatus status, Pageable pageable);
    
    Optional<Post> findByIdAndStatus(Long id, Post.PostStatus status);
    
    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.visibility = :visibility ORDER BY p.createdAt DESC")
    Page<Post> findByStatusAndVisibilityOrderByCreatedAtDesc(@Param("status") Post.PostStatus status,
                                                            @Param("visibility") Post.PostVisibility visibility,
                                                            Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND p.status = :status ORDER BY p.createdAt DESC")
    Page<Post> findByUserIdAndStatusOrderByCreatedAtDesc(@Param("userId") Long userId,
                                                        @Param("status") Post.PostStatus status,
                                                        Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.status = :status AND " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) ORDER BY p.createdAt DESC")
    Page<Post> findByStatusAndContentContainingIgnoreCaseOrderByCreatedAtDesc(@Param("status") Post.PostStatus status,
                                                                             @Param("search") String search,
                                                                             Pageable pageable);
    
    long countByUserAndStatus(User user, Post.PostStatus status);
    
    long countByStatus(Post.PostStatus status);
}
