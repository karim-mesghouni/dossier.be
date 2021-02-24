package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FilePhase;
import com.softline.dossier.be.domain.FilePhaseAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.File;

@Repository
public interface FilePhaseAgentRepository   extends JpaRepository<FilePhaseAgent,Long> {
    FilePhaseAgent getFilePhaseAgentByFilePhase_IdAndCurrentIsTrue(Long filePhaseId);
    FilePhaseAgent getFilePhaseAgentByFilePhase_IdAndCurrentIsFalseAndAndAssignedTo_Id(Long filePhaseId,Long assignedToId);
}
