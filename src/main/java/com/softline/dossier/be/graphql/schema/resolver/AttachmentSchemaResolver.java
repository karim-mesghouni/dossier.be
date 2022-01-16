package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Attachment;
import com.softline.dossier.be.domain.FileTaskAttachment;
import com.softline.dossier.be.repository.CommentAttachmentRepository;
import com.softline.dossier.be.repository.FileTaskAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AttachmentSchemaResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    private final FileTaskAttachmentRepository fileTaskAttachmentRepository;
    private final CommentAttachmentRepository commentAttachmentRepository;

    public List<Attachment> getAllForFileTask(Long fileTaskId) {
        return fileTaskAttachmentRepository.getAllForFileTask(fileTaskId)
                .stream().map(e -> (Attachment) e)
                .collect(Collectors.toList());
    }

    public List<Attachment> getAllForComment(Long commentId) {
        return commentAttachmentRepository.getAllForComment(commentId)
                .stream().map(e -> (Attachment) e)
                .collect(Collectors.toList());
    }

    public Boolean deleteFileTaskAttachment(Long attachmentId) {
        var fta = Database.findOrThrow(FileTaskAttachment.class, attachmentId);
        Database.removeNow(fta);
        if (fta.getPath().toFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            fta.getPath().toFile().delete();
        }
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
