package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.ReturnedCause;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnedCauseRepository extends JpaRepository<ReturnedCause, Long> {
}
