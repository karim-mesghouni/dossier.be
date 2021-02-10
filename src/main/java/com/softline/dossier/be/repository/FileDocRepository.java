package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDocRepository   extends JpaRepository<FileDoc,Long> {
}
