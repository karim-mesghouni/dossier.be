package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.AttachFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public interface AttachFileRepository extends JpaRepository<AttachFile,Long> {
    List<AttachFile> findAllByFileTask_Id(Long fileTaskId);
}
