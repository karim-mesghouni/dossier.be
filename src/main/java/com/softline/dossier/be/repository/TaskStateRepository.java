package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStateRepository extends JpaRepository<TaskState, Long>
{
}
