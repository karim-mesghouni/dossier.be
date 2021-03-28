package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileStateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FileStateTypeRepository   extends JpaRepository<FileStateType,Long> {
   FileStateType findFirstByInitialIsTrue();
}
