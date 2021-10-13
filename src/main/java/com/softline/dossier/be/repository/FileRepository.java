package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.repository.custom.FileRepositoryCustom;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom
{

    @Query("select f from File f where f.inTrash = false order by f.order")
    List<File> findAll();

    Optional<File> findById(Long aLong);

    @Query("select f from File f where f.order > :order order by f.order")
    List<File> findAllByOrderAfter(long order);

    @Query("select f from File f where f.order < :order order by f.order")
    List<File> findAllByOrderBefore(long order);

    //    @Query("select f from File f where f.order > least(:a, :b) and f.order < greater(:a, :b)")
    @Query("select count(f) from File f where (:a < :b and f.order > :a and f.order < :b) or (:a >= :b and f.order > :b and f.order < :a)")
    int countAllByOrderBetween(long a, long b);

    /**
     * get the min order of all files
     */
    @Query("select MIN(f.order) from File f")
    int minOrder();

    /**
     * increments the order of all files by 1
     * @return number of affected rows, should be the size of the table
     */
    @Modifying
    @Query("UPDATE File f set f.order = f.order + 1")
    int incrementAllOrder();
}
