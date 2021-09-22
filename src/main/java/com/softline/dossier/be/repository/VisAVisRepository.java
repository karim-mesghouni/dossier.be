package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.VisAVis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisAVisRepository extends JpaRepository<VisAVis, Long> {
}
