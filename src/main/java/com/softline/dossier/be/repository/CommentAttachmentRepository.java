package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.CommentAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentAttachmentRepository extends JpaRepository<CommentAttachment, Long>
{
    @Query("SELECT att from CommentAttachment att where att.comment.id = :commentId")
    List<CommentAttachment> getAllForComment(Long commentId);

    CommentAttachment findByStorageName(String storageName);
}
