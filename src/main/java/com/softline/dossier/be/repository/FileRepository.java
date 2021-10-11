package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.repository.custom.FileRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom {

    @Query("select  f from  File f where f.inTrash=false ")
    List<File> findAll();

    Optional<File> findById(Long aLong);

    @Query("select f from File f where f.order > :order")
    List<File> findAllByOrderAfter(long order);

    @Query("select f from File f where f.order < :order")
    List<File> findAllByOrderBefore(long order);

//    @Query("select f from File f where f.order > least(:a, :b) and f.order < greater(:a, :b)")
    @Query("select count(f) from File f where (:a < :b and f.order > :a and f.order < :b) or (:a >= :b and f.order > :b and f.order < :a)")
    int countAllByOrderBetween(long a, long b);
}
