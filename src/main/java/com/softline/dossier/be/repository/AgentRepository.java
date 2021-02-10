package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface AgentRepository extends JpaRepository<Agent,Long> {
}
