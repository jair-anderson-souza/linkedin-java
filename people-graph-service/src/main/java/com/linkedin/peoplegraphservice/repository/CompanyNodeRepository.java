package com.linkedin.peoplegraphservice.repository;

import com.linkedin.peoplegraphservice.entity.CompanyNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyNodeRepository extends Neo4jRepository<CompanyNode, Long> {
    
    Optional<CompanyNode> findByName(String name);
}
