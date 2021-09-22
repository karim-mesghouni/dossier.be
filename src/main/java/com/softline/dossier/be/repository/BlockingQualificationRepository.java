package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.BlockingQualification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface BlockingQualificationRepository extends JpaRepository<BlockingQualification, Long> {
}
