package com.linkedin.peoplegraphservice.entity;

import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;
import java.util.Objects;

@RelationshipProperties
public class SkillRelationship {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Property("endorsements")
    private Integer endorsements = 0;
    
    @Property("createdAt")
    private LocalDateTime createdAt;
    
    @TargetNode
    private SkillNode skill;
    
    // Constructors
    public SkillRelationship() {}
    
    public SkillRelationship(SkillNode skill) {
        this.skill = skill;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getEndorsements() {
        return endorsements;
    }
    
    public void setEndorsements(Integer endorsements) {
        this.endorsements = endorsements;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public SkillNode getSkill() {
        return skill;
    }
    
    public void setSkill(SkillNode skill) {
        this.skill = skill;
    }
    
    // Helper methods
    public void incrementEndorsements() {
        this.endorsements++;
    }
    
    public void decrementEndorsements() {
        if (this.endorsements > 0) {
            this.endorsements--;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillRelationship that = (SkillRelationship) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "SkillRelationship{" +
                "id=" + id +
                ", endorsements=" + endorsements +
                ", skill=" + (skill != null ? skill.getName() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}
