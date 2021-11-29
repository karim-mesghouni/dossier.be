package com.softline.dossier.be.service;

import com.softline.dossier.be.Tools.EnvUtil;
import com.softline.dossier.be.Tools.FileSystem;
import com.softline.dossier.be.Tools.TipTap;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.CommentType;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.CommentEvent;
import com.softline.dossier.be.events.entities.MessageEvent;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.repository.CommentRepository;
import com.softline.dossier.be.repository.FileActivityRepository;
import com.softline.dossier.be.repository.FileTaskRepository;
import com.softline.dossier.be.security.domain.Agent;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static com.softline.dossier.be.Tools.Functions.safeRun;
import static com.softline.dossier.be.security.domain.Agent.thisDBAgent;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "CommentService")
public class CommentService {
    private final FileActivityRepository fileActivityRepository;
    private final FileTaskRepository fileTaskRepository;
    private final CommentRepository repository;

    public List<Comment> getAll() {
        return Database.findAll(Comment.class);
    }

    public Comment create(CommentInput input) throws IOException {
        var fileActivity = fileActivityRepository.findById(input.getFileActivity().getId()).orElseThrow();
        var comment = Comment.builder()
                .fileActivity(FileActivity.builder()
                        .id(fileActivity.getId())
                        .activity(Activity.builder().id(fileActivity.getActivity().getId()).name(fileActivity.getActivity().getName()).build())
                        .file(File.builder().id(fileActivity.getFile().getId()).project(fileActivity.getFile().getProject()).build())
                        .build()
                )
                .content(input.getContent())
                .agent(thisDBAgent())
                .build();
        safeRun(() -> comment.setFileTask(fileTaskRepository.findById(input.getFileTask().getId()).orElseThrow()));
        comment.setType(CommentType.Comment);
        Database.startTransaction();
        Database.persist(comment);
        TipTap.resolveCommentContent(comment);
        Database.commit();
        new CommentEvent(EntityEvent.Type.ADDED, comment).fireToAll();
        comment.getMessages().forEach(m -> {
            if (m.isParsedNow()) {
                new MessageEvent(EntityEvent.Type.ADDED, m).fireTo(m.getTargetAgent());
            }
        });
        return comment;
    }

    public Comment update(CommentInput input) {
        return Database.findOrThrow(Comment.class, input, "UPDATE_COMMENT", comment -> {
            Database.startTransaction();
            comment.setContent(input.getContent());
            TipTap.resolveCommentContent(comment);
            Database.commit();
            new CommentEvent(EntityEvent.Type.UPDATED, comment).fireToAll();
            comment.getMessages().forEach(m -> {
                if (m.isParsedNow()) {
                    new MessageEvent(EntityEvent.Type.ADDED, m).fireTo(m.getTargetAgent());
                }
            });
            return comment;
        });
    }

    public boolean delete(long id) {
        return Database.findOrThrow(Comment.class, id, "DELETE_COMMENT", comment -> {
            Database.startTransaction();
            if (comment.getFileTask() != null) {
                if (comment.getType() == CommentType.Retour) {
                    comment.getFileTask().setRetour(null);
                } else {
                    if (comment.getType() == CommentType.Description) {
                        comment.getFileTask().setDescription(null);
                    }
                }
            }
            Database.remove(comment);
            Database.commit();
            new CommentEvent(EntityEvent.Type.DELETED, comment).fireToAll();
            return true;
        });
    }

    public Comment getById(long id) {
        return Database.findOrThrow(Comment.class, id);
    }

    public String saveFile(DataFetchingEnvironment environment) throws IOException {
        var file = (ApplicationPart) environment.getArgument("image");
        var storageName = FileSystem.randomMD5() + "." + FilenameUtils.getExtension(file.getSubmittedFileName());
        Files.copy(file.getInputStream(), FileSystem.getAttachmentsPath().resolve(storageName));
//        attachFileRepository.save(AttachFile.builder()
//                .storageName(storageName)
//                .realName(file.getSubmittedFileName())
//                .contentType(file.getContentType())
//                .fileTask(fileTask)
//                .build())
        return EnvUtil.getServerUrl() + "/attachments/" + storageName;
    }

    public List<Comment> getAllCommentByFileId(Long fileId) {
        return repository.findAllByFileActivity_File_Id(fileId);
    }

    public Message getMessageById(long messageId) {
        return Database.findOrThrow(Message.class, messageId, "READ_MESSAGE");
    }

    public List<Message> getAllMessagesForThisAgent() {
        return Database.query("SELECT m FROM Message m where m.targetAgent.id = :agentId order by m.readMessage asc, m.createdDate desc", Message.class)
                .setParameter("agentId", Agent.thisAgent().getId())
                .getResultList();
    }
}
