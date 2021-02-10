package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhaseRepository   extends JpaRepository<Phase,Long> {
}
