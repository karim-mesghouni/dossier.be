package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FilePhaseState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilePhaseStateRepository   extends JpaRepository<FilePhaseState,Long> {
}
