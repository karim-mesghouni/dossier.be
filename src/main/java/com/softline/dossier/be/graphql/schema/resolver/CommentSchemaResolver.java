package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.Comment;
import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.graphql.types.FileDTO;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.repository.CommentRepository;
import com.softline.dossier.be.repository.FileRepository;
import com.softline.dossier.be.service.CommentService;
import com.softline.dossier.be.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentSchemaResolver extends SchemaResolverBase<Comment, CommentInput, CommentRepository, CommentService> {


    public Comment createComment(CommentInput input){
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



}
