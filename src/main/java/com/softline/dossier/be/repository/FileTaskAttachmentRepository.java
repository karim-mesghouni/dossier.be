package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.FileTaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileTaskAttachmentRepository extends JpaRepository<FileTaskAttachment, Long> {
    @Query("SELECT att from FileTaskAttachment att where att.fileTask.id = :fileTaskId")
    List<FileTaskAttachment> getAllForFileTask(long fileTaskId);

    FileTaskAttachment findByStorageName(String storageName);
}
