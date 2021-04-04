package com.softline.dossier.be.security.repository;

import com.softline.dossier.be.security.domain.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface AgentRepository extends JpaRepository<Agent,Long> {
    Agent findByUsername(String userName);

}
