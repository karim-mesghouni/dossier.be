package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Comment;
import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;


@Getter
public class CommentInput extends Input<Comment> implements HasId {
    Class<Comment> mappingTarget = Comment.class;

    long id;
    String content;
    FileActivityInput fileActivity;
    FileTaskInput fileTask;
}
