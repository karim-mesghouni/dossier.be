package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileActivityRepository extends JpaRepository<FileActivity, Long>
{
    @Query("select  fa from  FileActivity fa where fa.file.id=?1 and fa.inTrash=false order by fa.order ")
    List<FileActivity> findAllByFile_Id(Long fileId);

    @Query("select  max(f.order) from  FileActivity f where f.inTrash=false ")
    Integer getMaxOrder();

    @Query("select fa from FileActivity fa where fa.file.id = :fileId and fa.order > :order order by fa.order")
    List<FileActivity> findAllByOrderAfter(long order, long fileId);

    @Query("select fa from FileActivity fa where fa.file.id = :fileId and fa.order < :order order by fa.order")
    List<FileActivity> findAllByOrderBefore(long order, long fileId);

    @Query("select count(fa) from FileActivity fa where fa.file.id = :fileId and ((:a < :b and fa.order > :a and fa.order < :b) or (:a >= :b and fa.order > :b and fa.order < :a))")
    int countAllByOrderBetween(long a, long b, long fileId);
}
