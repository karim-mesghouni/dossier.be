package com.softline.dossier.be.security.repository;

import com.softline.dossier.be.security.domain.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface AgentRepository extends JpaRepository<Agent, Long> {
    @Query("select a from Agent a where :search is null or a.username like %:search% or a.name like %:search% order by a.createdDate desc")
    List<Agent> findBySearch(String search);

    Agent findByUsername(String userName);
}
