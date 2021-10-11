package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileDocRepository extends JpaRepository<FileDoc, Long>
{
    List<FileDoc> findAllByFileActivity_Id(Long fileActivityId);

    List<FileDoc> findAllByFile_Id(Long fileId);
}
