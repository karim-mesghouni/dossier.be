package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileTaskRepository extends JpaRepository<FileTask,Long> {


    List<FileTask> findAllByFileActivity_Id(Long fileActivityId);
    List<FileTask> findAllByAssignedTo_Id(Long assignedToId);
}
