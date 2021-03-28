package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileTask;
import com.softline.dossier.be.domain.FileTaskSituation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;

@Repository
public interface FileTaskSituationRepository extends JpaRepository<FileTaskSituation,Long> {

    FileTaskSituation  findFirstByFileTaskAndCurrentIsTrue(FileTask fileTask);
}
