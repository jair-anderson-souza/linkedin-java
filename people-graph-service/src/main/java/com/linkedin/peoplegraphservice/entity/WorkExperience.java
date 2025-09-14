package com.linkedin.peoplegraphservice.entity;

import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@RelationshipProperties
public class WorkExperience {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Property("position")
    private String position;
    
    @Property("startDate")
    private LocalDate startDate;
    
    @Property("endDate")
    private LocalDate endDate;
    
    @Property("createdAt")
    private LocalDateTime createdAt;
    
    @TargetNode
    private CompanyNode company;
    
    // Constructors
    public WorkExperience() {}
    
    public WorkExperience(CompanyNode company, String position, LocalDate startDate, LocalDate endDate) {
        this.company = company;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public CompanyNode getCompany() {
        return company;
    }
    
    public void setCompany(CompanyNode company) {
        this.company = company;
    }
    
    // Helper methods
    public boolean isCurrent() {
        return endDate == null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkExperience that = (WorkExperience) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "WorkExperience{" +
                "id=" + id +
                ", position='" + position + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", company=" + (company != null ? company.getName() : null) +
                '}';
    }
}
