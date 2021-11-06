package com.softline.dossier.be.service;

import com.softline.dossier.be.Halpers.EnvUtil;
import com.softline.dossier.be.Halpers.FileSystem;
import com.softline.dossier.be.Halpers.Functions;
import com.softline.dossier.be.SSE.EventController;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.CommentType;
import com.softline.dossier.be.events.types.Event;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.repository.CommentRepository;
import com.softline.dossier.be.repository.FileActivityRepository;
import com.softline.dossier.be.repository.FileTaskRepository;
import com.softline.dossier.be.repository.MessageRepository;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.repository.AgentRepository;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.io.FilenameUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


@Transactional
@Service
@RequiredArgsConstructor
@Slf4j(topic = "CommentService")
public class CommentService extends IServiceBase<Comment, CommentInput, CommentRepository> {
    private final AgentRepository agentRepository;
    private final FileActivityRepository fileActivityRepository;
    private final FileTaskRepository fileTaskRepository;
    private final MessageRepository messageRepository;


    @Override
    public List<Comment> getAll() {
        return repository.findAll();
    }

    @Override
    public Comment create(CommentInput input) throws IOException {
        var fileActivity = fileActivityRepository.findById(input.getFileActivity().getId()).orElseThrow();
        var agnet = agentRepository.findById(input.getAgent().getId()).orElseThrow();
        var comment = Comment.builder()
                .fileActivity(FileActivity.builder()
                        .id(fileActivity.getId())
                        .activity(Activity.builder().id(fileActivity.getActivity().getId()).name(fileActivity.getActivity().getName()).build())
                        .file(File.builder().id(fileActivity.getFile().getId()).project(fileActivity.getFile().getProject()).build())
                        .build()
                )
                .content(input.getContent())
                .agent(Agent.builder().name(agnet.getName()).id(agnet.getId()).build())
                .build();
        Functions.safeRun(() -> comment.setFileTask(fileTaskRepository.findById(input.getFileTask().getId()).orElseThrow()));
        comment.setType(CommentType.Comment);
        getRepository().save(comment);
        EventController.sendForAllChannels(new Event<>("comment", comment));
        return comment;
    }

    @Override
    @SneakyThrows
    @PreAuthorize("hasPermission(#input.id, 'Comment', 'UPDATE_COMMENT')")
    public Comment update(CommentInput input) {
        var comment = repository.findWithAttachmentsById(input.getId());
        repository.save(comment);
        return comment;
    }

    @Override
    public boolean delete(long id) {
        var comment = repository.findById(id).orElseThrow();
        if (comment.getFileTask() != null) {
            if (comment.getType() == CommentType.Returned) {
                comment.getFileTask().setRetour(null);
            } else {
                if (comment.getType() == CommentType.Description) {
                    comment.getFileTask().setDescription(null);
                }
            }
        }
        repository.deleteById(id);
        return true;
    }

    @Override
    public Comment getById(long id) {
        return repository.findById(id).orElseThrow();
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
        return getRepository().findAllByFileActivity_File_Id(fileId);
    }

    public Message getMessageByIdForThisAgent(long messageId) {
        return messageRepository.findByIdAndAgent_Id(messageId, Agent.thisAgent().getId());
    }

    public List<Message> getAllMessagesForThisAgent() {
        return messageRepository.findAllByAgent_IdOrderByCreatedDateDesc(Agent.thisAgent().getId());
    }
}