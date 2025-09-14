package com.linkedin.peoplegraphservice.repository;

import com.linkedin.peoplegraphservice.entity.SkillNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillNodeRepository extends Neo4jRepository<SkillNode, String> {
}
