package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileActivityRepository extends JpaRepository<FileActivity,Long> {
}
