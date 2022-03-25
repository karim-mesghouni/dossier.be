package com.softline.dossier.be.taskmanger.repository;

import com.softline.dossier.be.taskmanger.domain.GenericTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericTaskRepository extends JpaRepository<GenericTask, Long> {
}
