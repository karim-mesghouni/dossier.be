package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.graphql.types.FileDTO;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.repository.AgentRepository;
import com.softline.dossier.be.repository.CommentRepository;
import com.softline.dossier.be.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class CommentService extends IServiceBase<Comment, CommentInput, CommentRepository> {

    @Override
    public List<Comment> getAll() {
        return repository.findAll();
    }

    @Override
    public Comment create(CommentInput input) {

        return getRepository().save(Comment.builder()
                .file(File.builder().id(input.getFile().getId()).build())
                .content(input.getContent())
                .agent(Agent.builder().id(input.getAgent().getId()).name(input.getAgent().getName()).build())
                .build()
        );
    }

    @Override
    public Comment update(CommentInput input) {
        var comment =repository.findById(input.getId()).orElseThrow();
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

}