package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Comment;
import com.softline.dossier.be.domain.Message;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.repository.CommentRepository;
import com.softline.dossier.be.repository.MessageRepository;
import com.softline.dossier.be.service.CommentService;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.Part;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.softline.dossier.be.Application.context;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class CommentSchemaResolver extends SchemaResolverBase<Comment, CommentInput, CommentRepository, CommentService> {


    public Comment createComment(CommentInput input) throws IOException, ClientReadableException {
        return create(input);
    }

    public Comment updateComment(CommentInput input) throws ClientReadableException {
        return update(input);
    }

    public boolean deleteComment(Long id) throws ClientReadableException {
        return delete(id);
    }

    public List<Comment> getAllComment() {
        return getAll();
    }

    public Comment getComment(Long id) {
        return get(id);
    }

    public String uploadImage(Part part, DataFetchingEnvironment environment) throws IOException, NoSuchAlgorithmException {
        return service.saveFile(environment);
    }

    public List<Comment> getAllCommentByFileId(Long fileId) {
        return service.getAllCommentByFileId(fileId);

    }

    @PostFilter("hasPermission(filterObject, 'READ_MESSAGE')")
    public List<Message> allMessagesForThisAgent() {
        return service.getAllMessagesForThisAgent();
    }

    @PostAuthorize("hasPermission(returnObject, 'READ_MESSAGE')")
    public Message messageByIdForThisAgent(long messageId) {
        return service.getMessageByIdForThisAgent(messageId);
    }

    @PostAuthorize("hasPermission(#messageId, 'Message', 'READ_MESSAGE')")
    public boolean readMessage(long messageId) {
        Message message = context().getBean(MessageRepository.class).findById(messageId).orElseThrow();
        message.read();
        context().getBean(MessageRepository.class).save(message);
        return true;
    }
}
