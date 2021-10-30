package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.Reprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepriseRepository extends JpaRepository<Reprise, Long> {
}
