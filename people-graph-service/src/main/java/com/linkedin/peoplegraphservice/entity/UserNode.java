package com.linkedin.peoplegraphservice.entity;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Node("User")
public class UserNode {
    
    @Id
    private Long id;
    
    @Property("email")
    private String email;
    
    @Property("firstName")
    private String firstName;
    
    @Property("lastName")
    private String lastName;
    
    @Property("headline")
    private String headline;
    
    @Property("location")
    private String location;
    
    @Property("industry")
    private String industry;
    
    @Property("createdAt")
    private LocalDateTime createdAt;
    
    @Relationship(type = "CONNECTED_TO", direction = Relationship.Direction.OUTGOING)
    private Set<UserNode> connections = new HashSet<>();
    
    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    private Set<CompanyNode> followedCompanies = new HashSet<>();
    
    @Relationship(type = "WORKED_AT", direction = Relationship.Direction.OUTGOING)
    private Set<WorkExperience> workExperiences = new HashSet<>();
    
    @Relationship(type = "HAS_SKILL", direction = Relationship.Direction.OUTGOING)
    private Set<SkillRelationship> skills = new HashSet<>();
    
    @Relationship(type = "ENDORSED", direction = Relationship.Direction.OUTGOING)
    private Set<Endorsement> endorsements = new HashSet<>();
    
    // Constructors
    public UserNode() {}
    
    public UserNode(Long id, String email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getHeadline() {
        return headline;
    }
    
    public void setHeadline(String headline) {
        this.headline = headline;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Set<UserNode> getConnections() {
        return connections;
    }
    
    public void setConnections(Set<UserNode> connections) {
        this.connections = connections;
    }
    
    public Set<CompanyNode> getFollowedCompanies() {
        return followedCompanies;
    }
    
    public void setFollowedCompanies(Set<CompanyNode> followedCompanies) {
        this.followedCompanies = followedCompanies;
    }
    
    public Set<WorkExperience> getWorkExperiences() {
        return workExperiences;
    }
    
    public void setWorkExperiences(Set<WorkExperience> workExperiences) {
        this.workExperiences = workExperiences;
    }
    
    public Set<SkillRelationship> getSkills() {
        return skills;
    }
    
    public void setSkills(Set<SkillRelationship> skills) {
        this.skills = skills;
    }
    
    public Set<Endorsement> getEndorsements() {
        return endorsements;
    }
    
    public void setEndorsements(Set<Endorsement> endorsements) {
        this.endorsements = endorsements;
    }
    
    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public void addConnection(UserNode user) {
        this.connections.add(user);
        user.getConnections().add(this);
    }
    
    public void removeConnection(UserNode user) {
        this.connections.remove(user);
        user.getConnections().remove(this);
    }
    
    public void followCompany(CompanyNode company) {
        this.followedCompanies.add(company);
    }
    
    public void unfollowCompany(CompanyNode company) {
        this.followedCompanies.remove(company);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNode userNode = (UserNode) o;
        return Objects.equals(id, userNode.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "UserNode{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", headline='" + headline + '\'' +
                ", location='" + location + '\'' +
                ", industry='" + industry + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
