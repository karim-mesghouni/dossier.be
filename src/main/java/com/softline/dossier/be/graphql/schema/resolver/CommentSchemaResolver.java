package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Comment;
import com.softline.dossier.be.domain.Message;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.service.CommentService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CommentSchemaResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    private final CommentService service;

    public Comment createComment(CommentInput input) throws IOException {
        return service.create(input);
    }

    public Comment updateComment(CommentInput input) {
        return service.update(input);
    }

    public boolean deleteComment(Long id) {
        return service.delete(id);
    }

    public List<Comment> getAllComment() {
        return service.getAll();
    }

    public Comment getComment(Long id) {
        return service.getById(id);
    }

    public String uploadImage(Part part, DataFetchingEnvironment environment) throws IOException {
        return service.saveFile(environment);
    }

    public List<Comment> getAllCommentByFileId(Long fileId) {
        return service.getAllCommentByFileId(fileId);

    }

    public List<Message> allMessagesForThisAgent() {
        return service.getAllMessagesForThisAgent();
    }

    public Message messageById(long messageId) {
        return service.getMessageById(messageId);
    }

    public boolean readMessage(long messageId) {
        return Database.findOrThrow(Message.class, messageId, message -> {
            if (message.getTargetAgent().getId() == Agent.thisAgent().getId()) {
                Database.startTransaction();
                message.read();
                Database.commit();
                return true;
            }
            return false;
        });
    }
}
