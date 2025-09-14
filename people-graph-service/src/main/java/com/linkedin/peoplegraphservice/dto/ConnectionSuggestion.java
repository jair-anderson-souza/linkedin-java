package com.linkedin.peoplegraphservice.dto;

public class ConnectionSuggestion {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String headline;
    private String industry;
    private String location;
    private Integer mutualFriends;
    private Integer relevanceScore;
    private Integer affinityScore;
    
    // Constructors
    public ConnectionSuggestion() {}
    
    public ConnectionSuggestion(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Integer getMutualFriends() {
        return mutualFriends;
    }
    
    public void setMutualFriends(Integer mutualFriends) {
        this.mutualFriends = mutualFriends;
    }
    
    public Integer getRelevanceScore() {
        return relevanceScore;
    }
    
    public void setRelevanceScore(Integer relevanceScore) {
        this.relevanceScore = relevanceScore;
    }
    
    public Integer getAffinityScore() {
        return affinityScore;
    }
    
    public void setAffinityScore(Integer affinityScore) {
        this.affinityScore = affinityScore;
    }
    
    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "ConnectionSuggestion{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", headline='" + headline + '\'' +
                ", industry='" + industry + '\'' +
                ", location='" + location + '\'' +
                ", mutualFriends=" + mutualFriends +
                ", relevanceScore=" + relevanceScore +
                ", affinityScore=" + affinityScore +
                '}';
    }
}
