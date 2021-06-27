package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.domain.FileTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileActivityRepository extends JpaRepository<FileActivity,Long> {
    @Query("select  fa from  FileActivity fa where fa.file.id=?1 and fa.inTrash=false ")
    List<FileActivity> findAllByFile_Id(Long fileId);
    @Query("select  distinct(fa) from  FileActivity fa JOIN FETCH fa.fileTasks fs  where fa.file.id=?1 and  fs.inTrash =true  ")
    List<FileActivity> findAllByFile_Id_In_TrashWithTask(Long fileId);
    @Query("select  distinct(fa) from  FileActivity fa   where fa.file.id=?1 and fa.inTrash=true  ")
    List<FileActivity> findAllByFile_Id_In_Trash(Long fileId);
    @Query("select  fa from  FileActivity fa  where fa.file.id=?1 and fa.current =true and fa.inTrash=false  ")
    FileActivity findFirstByCurrentIsTrueAndFile_Id(Long fileId);

}
