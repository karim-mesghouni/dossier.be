package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.Blocking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockingRepository extends JpaRepository<Blocking, Long>
{
    List<Blocking> findAllByState_FileTask_Id(Long fileTaskId);
}
