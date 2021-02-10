package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.PhaseState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhaseStateRepository   extends JpaRepository<PhaseState,Long> {
}
