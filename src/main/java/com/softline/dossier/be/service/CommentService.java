package com.softline.dossier.be.service;

import com.softline.dossier.be.Halpers.ImageHalper;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.CommentType;
import com.softline.dossier.be.graphql.types.FileDTO;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.repository.*;
import com.softline.dossier.be.security.repository.AgentRepository;
import graphql.schema.DataFetchingEnvironment;
import org.apache.catalina.core.ApplicationPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.*;

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

    @Override
    public List<Comment> getAll() {
        return repository.findAll();
    }

    @Override
    public Comment create(CommentInput input) {
        var fileActivity = fileActivityRepository.findById(input.getFileActivity().getId()).orElseThrow();
        var comment = Comment.builder()
                .fileActivity(fileActivity)
                .content(input.getContent())
                .agent(agentRepository.findById(input.getAgent().getId()).orElseThrow())
                .build();
        if (input.getFileTask() != null && input.getFileTask().getId() != null) {
            var fileTask = fileTaskRepository.findById(input.getFileTask().getId()).orElseThrow();
            comment.setFileTask(fileTask);
        }
        comment.setType(CommentType.Comment);
        return getRepository().save(
                comment
        );
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
}