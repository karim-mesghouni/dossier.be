package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.File;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FileRepository extends JpaRepository<File, Long>
{

    @NotNull
    @Query("select f from File f where f.inTrash = false order by f.order")
    List<File> findAll();

    @Query("select f from File f order by f.order")
    List<File> findAllWithTrashed();

    @NotNull
    Optional<File> findById(@NotNull Long aLong);

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
     */
    @Modifying
    @Query("UPDATE File f set f.order = f.order + 1")
    void incrementAllOrder();
}
