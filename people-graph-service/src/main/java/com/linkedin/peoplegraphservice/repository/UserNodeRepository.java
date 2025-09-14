package com.linkedin.peoplegraphservice.repository;

import com.linkedin.peoplegraphservice.entity.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNodeRepository extends Neo4jRepository<UserNode, Long> {
    
    Optional<UserNode> findByEmail(String email);
    
    // Connection suggestions based on mutual connections
    @Query("MATCH (u:User {id: $userId})-[:CONNECTED_TO]-(friend:User)-[:CONNECTED_TO]-(suggestion:User) " +
           "WHERE suggestion.id <> $userId AND NOT (u)-[:CONNECTED_TO]-(suggestion) " +
           "WITH suggestion, count(DISTINCT friend) as mutualFriends " +
           "ORDER BY mutualFriends DESC " +
           "LIMIT $limit " +
           "RETURN suggestion, mutualFriends")
    List<UserNode> findConnectionSuggestions(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    // Find shortest path between two users
    @Query("MATCH (u1:User {id: $fromUserId}), (u2:User {id: $toUserId}) " +
           "MATCH path = shortestPath((u1)-[:CONNECTED_TO*]-(u2)) " +
           "RETURN nodes(path) as path, length(path) as pathLength")
    List<Object> findShortestPath(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);
    
    // People you may know - complex algorithm
    @Query("MATCH (u:User {id: $userId}) " +
           "OPTIONAL MATCH (u)-[:WORKED_AT]->(c:Company)<-[:WORKED_AT]-(colleague:User) " +
           "WHERE colleague.id <> $userId AND NOT (u)-[:CONNECTED_TO]-(colleague) " +
           "OPTIONAL MATCH (sameIndustry:User) " +
           "WHERE sameIndustry.industry = u.industry AND sameIndustry.id <> $userId " +
           "AND NOT (u)-[:CONNECTED_TO]-(sameIndustry) " +
           "OPTIONAL MATCH (sameLocation:User) " +
           "WHERE sameLocation.location = u.location AND sameLocation.id <> $userId " +
           "AND NOT (u)-[:CONNECTED_TO]-(sameLocation) " +
           "OPTIONAL MATCH (u)-[:HAS_SKILL]->(skill:Skill)<-[:HAS_SKILL]-(skillMatch:User) " +
           "WHERE skillMatch.id <> $userId AND NOT (u)-[:CONNECTED_TO]-(skillMatch) " +
           "WITH collect(DISTINCT colleague) + collect(DISTINCT sameIndustry) + " +
           "collect(DISTINCT sameLocation) + collect(DISTINCT skillMatch) as suggestions " +
           "UNWIND suggestions as suggestion " +
           "WITH suggestion, CASE WHEN suggestion IS NOT NULL THEN 1 ELSE 0 END as score " +
           "WHERE suggestion IS NOT NULL " +
           "RETURN DISTINCT suggestion, sum(score) as relevanceScore " +
           "ORDER BY relevanceScore DESC " +
           "LIMIT $limit")
    List<UserNode> findPeopleYouMayKnow(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    // Get connection count for a user
    @Query("MATCH (u:User {id: $userId})-[:CONNECTED_TO]-(connected:User) " +
           "RETURN count(connected)")
    Integer getConnectionCount(@Param("userId") Long userId);
    
    // Get mutual connections between two users
    @Query("MATCH (u1:User {id: $userId1})-[:CONNECTED_TO]-(mutual:User)-[:CONNECTED_TO]-(u2:User {id: $userId2}) " +
           "WHERE u1.id <> u2.id " +
           "RETURN mutual")
    List<UserNode> findMutualConnections(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    // Affinity ranking based on multiple factors
    @Query("MATCH (u:User {id: $userId}) " +
           "MATCH (other:User) " +
           "WHERE other.id <> $userId AND NOT (u)-[:CONNECTED_TO]-(other) " +
           "OPTIONAL MATCH (u)-[:CONNECTED_TO]-(mutual:User)-[:CONNECTED_TO]-(other) " +
           "WITH u, other, count(DISTINCT mutual) as mutualConnections " +
           "OPTIONAL MATCH (u)-[:HAS_SKILL]->(skill:Skill)<-[:HAS_SKILL]-(other) " +
           "WITH u, other, mutualConnections, count(DISTINCT skill) as commonSkills " +
           "OPTIONAL MATCH (u)-[:WORKED_AT]->(company:Company)<-[:WORKED_AT]-(other) " +
           "WITH u, other, mutualConnections, commonSkills, count(DISTINCT company) as commonCompanies " +
           "WITH other, " +
           "(mutualConnections * 3 + commonSkills * 2 + commonCompanies * 4 + " +
           "CASE WHEN other.industry = u.industry THEN 2 ELSE 0 END + " +
           "CASE WHEN other.location = u.location THEN 1 ELSE 0 END) as affinityScore " +
           "WHERE affinityScore > 0 " +
           "RETURN other, affinityScore " +
           "ORDER BY affinityScore DESC " +
           "LIMIT $limit")
    List<UserNode> findAffinityRanking(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    // Connect two users
    @Query("MATCH (u1:User {id: $fromUserId}), (u2:User {id: $toUserId}) " +
           "MERGE (u1)-[r:CONNECTED_TO]-(u2) " +
           "SET r.connectedAt = datetime() " +
           "RETURN r")
    void connectUsers(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);
    
    // Follow company
    @Query("MATCH (u:User {id: $userId}), (c:Company {id: $companyId}) " +
           "MERGE (u)-[r:FOLLOWS]->(c) " +
           "SET r.followedAt = datetime() " +
           "RETURN r")
    void followCompany(@Param("userId") Long userId, @Param("companyId") Long companyId);
    
    // Add work experience
    @Query("MATCH (u:User {id: $userId}), (c:Company {id: $companyId}) " +
           "CREATE (u)-[r:WORKED_AT]->(c) " +
           "SET r.position = $position, " +
           "r.startDate = date($startDate), " +
           "r.endDate = CASE WHEN $endDate IS NOT NULL THEN date($endDate) ELSE null END, " +
           "r.createdAt = datetime() " +
           "RETURN r")
    void addWorkExperience(@Param("userId") Long userId, 
                          @Param("companyId") Long companyId, 
                          @Param("position") String position, 
                          @Param("startDate") String startDate, 
                          @Param("endDate") String endDate);
}
