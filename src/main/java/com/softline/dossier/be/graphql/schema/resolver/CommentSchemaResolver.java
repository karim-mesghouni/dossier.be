package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Comment;
import com.softline.dossier.be.domain.Message;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.graphql.types.input.NotifyMessageInput;
import com.softline.dossier.be.repository.CommentRepository;
import com.softline.dossier.be.service.CommentService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.Part;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class CommentSchemaResolver extends SchemaResolverBase<Comment, CommentInput, CommentRepository, CommentService> {


    public Comment createComment(CommentInput input) throws IOException {
        return create(input);
    }
    public Comment updateComment(CommentInput input){
        return update(input);
    }
    public boolean deleteComment(Long id){
        return delete(id);
    }
    public List<Comment> getAllComment(){
        return getAll();
    }
    public Comment getComment(Long id){
        return get(id);
    }
    public String uploadImage(Part part, DataFetchingEnvironment environment) throws IOException, NoSuchAlgorithmException {
        return  service.saveFile(environment);
    }

  public  List<Comment>  getAllCommentByFileId(Long fileId){
      return  service.getAllCommentByFileId(fileId);

  }
  public boolean     notifyMessage(NotifyMessageInput input){
    return   service.notifyMessage(input);
  }
  public List<Message>  getMessages(Long agentId)
  {
      return  service.getMessages(agentId);
  }
}
