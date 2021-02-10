package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository   extends JpaRepository<File,Long> {
}
