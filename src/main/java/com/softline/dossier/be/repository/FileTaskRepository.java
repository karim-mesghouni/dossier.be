package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileTaskRepository extends JpaRepository<FileTask, Long> {

    @Query("select  ft from  FileTask ft where ft.fileActivity.id=?1 and ft.inTrash=false order by ft.order")
    List<FileTask> findAllByFileActivity_Id(Long fileActivityId);

    @Query("select  ft from  FileTask ft where ft.fileActivity.id=?1 and ft.inTrash=true order by ft.order")
    List<FileTask> findAllByFileActivity_Id_In_Trash(Long fileActivityId);

    @Query("select  ft from  FileTask ft where ft.assignedTo.id=?1 and ft.inTrash=false order by ft.order")
    List<FileTask> findAllByAssignedTo_Id(Long assignedToId);

    Long countFileTaskByFileActivity_File_Id(Long fileId);
    @Query("select ft from FileTask ft where ft.fileActivity.id = :fileActivityId and ft.order > :order order by ft.order")
    List<FileTask> findAllByOrderAfter(long order, long fileActivityId);

    @Query("select ft from FileTask ft where ft.fileActivity.id = :fileActivityId and ft.order < :order order by ft.order")
    List<FileTask> findAllByOrderBefore(long order, long fileActivityId);

    @Query("select count(ft) from FileTask ft where ft.fileActivity.id = :fileActivityId and ((:a < :b and ft.order > :a and ft.order < :b) or (:a >= :b and ft.order > :b and ft.order < :a))")
    int countAllByOrderBetween(long a, long b, long fileActivityId);
}
