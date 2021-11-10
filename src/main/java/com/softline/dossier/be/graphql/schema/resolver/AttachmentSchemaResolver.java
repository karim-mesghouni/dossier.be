package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.Tools.FileSystem;
import com.softline.dossier.be.domain.Attachment;
import com.softline.dossier.be.domain.FileTaskAttachment;
import com.softline.dossier.be.repository.CommentAttachmentRepository;
import com.softline.dossier.be.repository.FileTaskAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AttachmentSchemaResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    private final FileTaskAttachmentRepository fileTaskAttachmentRepository;
    private final CommentAttachmentRepository commentAttachmentRepository;
    private final FileSystem fileSystem;
    private final EntityManager entityManager;

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
        var res = fileTaskAttachmentRepository.findById(attachmentId);
        if (res.isEmpty()) {
            return false;
        } else {
            FileTaskAttachment attachment = res.get();
            fileTaskAttachmentRepository.delete(attachment);
            try {
                if (!attachment.getPath(fileSystem).toFile().delete()) {
                    throw new FileNotFoundException();
                }
            } catch (Exception e) {
                attachment.setDeleted(false);
                fileTaskAttachmentRepository.save(attachment);
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    public Boolean deleteCommentAttachment(Long attachmentId) {
        if (!commentAttachmentRepository.existsById(attachmentId)) {
            return false;
        }
        commentAttachmentRepository.deleteById(attachmentId);
        return true;
    }
}
