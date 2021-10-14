package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileActivityRepository extends JpaRepository<FileActivity, Long>
{
    @Query("select  fa from  FileActivity fa where fa.file.id=?1 and fa.inTrash=false order by fa.order ")
    List<FileActivity> findAllByFile_Id(Long fileId);

    @Query("select  distinct(fa) from  FileActivity fa JOIN FETCH fa.fileTasks fs  where fa.file.id=?1 and  fs.inTrash =true  ")
    List<FileActivity> findAllByFile_Id_In_TrashWithTask(Long fileId);

    @Query("select  distinct(fa) from  FileActivity fa   where fa.file.id=?1 and fa.inTrash=true  ")
    List<FileActivity> findAllByFile_Id_In_Trash(Long fileId);

    @Query("select  fa from  FileActivity fa  where fa.file.id=?1 and fa.current =true and fa.inTrash=false  ")
    FileActivity findFirstByCurrentIsTrueAndFile_Id(Long fileId);

    @Query("select  max(f.order) from  FileActivity f where f.inTrash=false ")
    Integer getMaxOrder();

    List<FileActivity> getFileByOrder(long fileOrder);

    @Query("select fa from FileActivity fa where fa.file.id = :fileId and fa.order > :order order by fa.order")
    List<FileActivity> findAllByOrderAfter(long order, long fileId);

    @Query("select fa from FileActivity fa where fa.file.id = :fileId and fa.order < :order order by fa.order")
    List<FileActivity> findAllByOrderBefore(long order, long fileId);

    @Query("select count(fa) from FileActivity fa where fa.file.id = :fileId and ((:a < :b and fa.order > :a and fa.order < :b) or (:a >= :b and fa.order > :b and fa.order < :a))")
    int countAllByOrderBetween(long a, long b, long fileId);

    /**
     * get the min order of all file activities associated with a file
     */
    @Query("select MIN(fa.order) from FileActivity fa where fa.file.id = :fileId")
    int minOrder(long fileId);

    /**
     * increments the order of all file activities associated with a file by 1
     *
     * @return number of affected rows, should be the size of the associated file activites
     */
    @Modifying
    @Query("UPDATE FileActivity fa set fa.order = fa.order + 1 where fa.file.id = :fileId")
    int incrementAllOrder(long fileId);
}
