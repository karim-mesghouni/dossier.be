package com.softline.dossier.be.service;

import com.softline.dossier.be.Halpers.ImageHalper;
import com.softline.dossier.be.Sse.model.EventDto;
import com.softline.dossier.be.Sse.service.SseNotificationService;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.CommentType;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.graphql.types.input.NotifyMessageInput;
import com.softline.dossier.be.repository.CommentRepository;
import com.softline.dossier.be.repository.FileActivityRepository;
import com.softline.dossier.be.repository.FileTaskRepository;
import com.softline.dossier.be.repository.MessageRepository;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.repository.AgentRepository;
import graphql.schema.DataFetchingEnvironment;
import org.apache.catalina.core.ApplicationPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;


@Transactional
@Service
public class CommentService extends IServiceBase<Comment, CommentInput, CommentRepository> {
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    AgentRepository agentRepository;

    @Autowired
    FileActivityRepository fileActivityRepository;

    @Autowired
    FileTaskRepository fileTaskRepository;

    @Autowired
    SseNotificationService sseNotificationService;
    @Autowired
    MessageRepository messageRepository;

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
        if (input.getFileTask() != null && input.getFileTask().getId() != null) {
            var fileTask = fileTaskRepository.findById(input.getFileTask().getId()).orElseThrow();
            comment.setFileTask(fileTask);
        }
        comment.setType(CommentType.Comment);
        getRepository().save(
                comment
        );
        var currentAgent = agentRepository.findByUsername(((Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        sseNotificationService.sendNotification(currentAgent.getId(), EventDto.builder().type("comment").body(comment).build());
        return comment;
    }

    @Override
    public Comment update(CommentInput input) {
        var comment = repository.findById(input.getId()).orElseThrow();
        comment.setContent(input.getContent());
        return comment;
    }

    @Override
    public boolean delete(long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public Comment getById(long id) {
        return repository.findById(id).orElseThrow();
    }

    public String saveFile(DataFetchingEnvironment environment) throws IOException, NoSuchAlgorithmException {
        var file = (ApplicationPart) environment.getArgument("image");

        var fileName = ImageHalper.getFilename(20L, file);

        var savedFile = new java.io.File(resourceLoader.getResource(new java.io.File("C:\\Users\\PC\\Documents\\fileStorage").toURI().toString()).getFile(), fileName);
        Files.copy(file.getInputStream(), savedFile.toPath());

        return "http://localhost:8081/images/" + fileName;
    }

    public List<Comment> getAllCommentByFileId(Long fileId) {
        return getRepository().findAllByFileActivity_File_Id(fileId);
    }

    public boolean notifyMessage(NotifyMessageInput input) {
        if (input.getAgentIds() != null) {
            var comment = getRepository().findById(input.getIdComment()).orElseThrow();
            var messages = input.getAgentIds().stream().distinct().map(agentId ->
                    Message.builder()
                            .readMessage(false)
                            .comment(Comment.builder().id(comment.getId())
                            .fileActivity(FileActivity.builder().file(File.builder().id(comment.getFileActivity().getFile().getId())
                                    .project(comment.getFileActivity().getFile().getProject())
                                    .build())
                            .activity(Activity.builder().id(comment.getFileActivity().getActivity().getId())
                                    .name(comment.getFileActivity().getActivity().getName())
                                    .build()).build())
                            .fileTask(comment.getFileTask()!=null?FileTask.builder().id(comment.getFileTask().getId()).number(comment.getFileTask().getNumber())
                                    .task(Task.builder().id(comment.getFileTask().getTask().getId()).name(comment.getFileTask().getTask().getName()).build()).build():null)
                            .agent(Agent.builder().id(comment.getAgent().getId()).name(comment.getAgent().getName()).build()).build()).
                            agent(Agent.builder().id(agentId).build()).build()
            ).collect(Collectors.toList());
            messageRepository.saveAll(messages);
            messages.forEach(x -> {
                try {
                    sseNotificationService.sendNotification(x.getAgent().getId(), EventDto.builder().type("message").body(x).build());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            return true;
        }
        return false;
    }
    public List<Message> getMessages(Long agentId){
        return  messageRepository.findAllByAgent_Id(agentId, Sort.by(Sort.Direction.DESC,Message_.CREATED_DATE));
    }
}