package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileStateRepository   extends JpaRepository<FileState,Long> {
}
