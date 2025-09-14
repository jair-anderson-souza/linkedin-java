package com.linkedin.peoplegraphservice.entity;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDateTime;
import java.util.Objects;

@Node("Skill")
public class SkillNode {
    
    @Id
    private String name;
    
    @Property("createdAt")
    private LocalDateTime createdAt;
    
    // Constructors
    public SkillNode() {}
    
    public SkillNode(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillNode skillNode = (SkillNode) o;
        return Objects.equals(name, skillNode.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return "SkillNode{" +
                "name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
