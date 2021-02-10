package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FilePhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilePhaseRepository   extends JpaRepository<FilePhase,Long> {
}
