package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.domain.FileTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileTaskRepository extends JpaRepository<FileTask,Long> {

    @Query("select  ft from  FileTask ft where ft.fileActivity.id=?1 and ft.inTrash=false order by ft.fileTaskOrder")
    List<FileTask> findAllByFileActivity_Id(Long fileActivityId);
    @Query("select  ft from  FileTask ft where ft.fileActivity.id=?1 and ft.inTrash=true ")
    List<FileTask> findAllByFileActivity_Id_In_Trash(Long fileActivityId);

    @Query("select  ft from  FileTask ft where ft.assignedTo.id=?1 and ft.inTrash=false ")

    List<FileTask> findAllByAssignedTo_Id(Long assignedToId);
    Long countFileTaskByFileActivity_File_Id(Long fileId);
    List<FileTask> findAll();
    Optional<FileTask> findById(Long aLong);
    @Query("select  max(f.fileTaskOrder) from  FileTask f where f.inTrash=false ")
    int getMaxOrder();
    List<FileTask> getAllByFileTaskOrder(int fileOrder);
}
