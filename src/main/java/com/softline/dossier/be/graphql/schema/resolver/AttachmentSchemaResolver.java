package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.CommentAttachment;
import com.softline.dossier.be.domain.FileTaskAttachment;
import com.softline.dossier.be.repository.CommentAttachmentRepository;
import com.softline.dossier.be.repository.FileTaskAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AttachmentSchemaResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    private final FileTaskAttachmentRepository fileTaskAttachmentRepository;
    private final CommentAttachmentRepository commentAttachmentRepository;

    public List<FileTaskAttachment> getAllForFileTask(Long fileTaskId) {
        return fileTaskAttachmentRepository.getAllForFileTask(fileTaskId);
    }

    public List<CommentAttachment> getAllForComment(Long commentId) {
        return commentAttachmentRepository.getAllForComment(commentId);
    }

    public Boolean deleteFileTaskAttachment(Long attachmentId) {
        var fta = Database.findOrThrow(FileTaskAttachment.class, attachmentId);
        Database.removeNow(fta);
        return true;
    }

    public Boolean deleteCommentAttachment(Long attachmentId) {
        if (!commentAttachmentRepository.existsById(attachmentId)) {
            return false;
        }
        commentAttachmentRepository.deleteById(attachmentId);
        return true;
    }
}
