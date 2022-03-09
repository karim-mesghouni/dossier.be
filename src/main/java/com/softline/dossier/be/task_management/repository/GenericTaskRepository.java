package com.softline.dossier.be.task_management.repository;

import com.softline.dossier.be.task_management.domain.GenericTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericTaskRepository extends JpaRepository<GenericTask, Long> {
}
