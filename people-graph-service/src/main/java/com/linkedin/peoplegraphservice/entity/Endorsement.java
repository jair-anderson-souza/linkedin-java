package com.linkedin.peoplegraphservice.entity;

import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;
import java.util.Objects;

@RelationshipProperties
public class Endorsement {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Property("skillName")
    private String skillName;
    
    @Property("endorsedUserId")
    private Long endorsedUserId;
    
    @Property("endorsedAt")
    private LocalDateTime endorsedAt;
    
    @TargetNode
    private UserNode endorsedUser;
    
    // Constructors
    public Endorsement() {}
    
    public Endorsement(UserNode endorsedUser, String skillName, Long endorsedUserId) {
        this.endorsedUser = endorsedUser;
        this.skillName = skillName;
        this.endorsedUserId = endorsedUserId;
        this.endorsedAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSkillName() {
        return skillName;
    }
    
    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
    
    public Long getEndorsedUserId() {
        return endorsedUserId;
    }
    
    public void setEndorsedUserId(Long endorsedUserId) {
        this.endorsedUserId = endorsedUserId;
    }
    
    public LocalDateTime getEndorsedAt() {
        return endorsedAt;
    }
    
    public void setEndorsedAt(LocalDateTime endorsedAt) {
        this.endorsedAt = endorsedAt;
    }
    
    public UserNode getEndorsedUser() {
        return endorsedUser;
    }
    
    public void setEndorsedUser(UserNode endorsedUser) {
        this.endorsedUser = endorsedUser;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endorsement that = (Endorsement) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Endorsement{" +
                "id=" + id +
                ", skillName='" + skillName + '\'' +
                ", endorsedUserId=" + endorsedUserId +
                ", endorsedAt=" + endorsedAt +
                ", endorsedUser=" + (endorsedUser != null ? endorsedUser.getFullName() : null) +
                '}';
    }
}
