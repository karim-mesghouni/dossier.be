package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository   extends JpaRepository<Job,Long> {
}
