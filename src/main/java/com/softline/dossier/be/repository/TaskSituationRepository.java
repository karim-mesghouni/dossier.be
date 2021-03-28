package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.TaskSituation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskSituationRepository extends JpaRepository<TaskSituation,Long> {
    List<TaskSituation> findAllByTask_Id(Long taskId);
}
