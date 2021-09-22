package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.Commune;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommuneRepository extends JpaRepository<Commune, Long> {
}
