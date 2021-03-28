package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileActivityRepository extends JpaRepository<FileActivity,Long> {
    List<FileActivity> findAllByFile_Id(Long fileId);
    FileActivity findFirstByCurrentIsTrueAndFile_Id(Long fileId);
}
