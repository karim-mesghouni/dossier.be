package com.softline.task.manger.repository;

import com.softline.task.manger.domain.FileActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileActivityRepository extends JpaRepository<FileActivity,Long> {
}
