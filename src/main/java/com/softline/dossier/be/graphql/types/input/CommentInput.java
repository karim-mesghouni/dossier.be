package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Comment;
import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.domain.enums.CommentType;
import lombok.Getter;


@Getter
public class CommentInput extends Input<Comment> implements HasId {
    Class<Comment> mappingTarget = Comment.class;

    Long id;
    String content;
    FileActivityInput fileActivity;
    FileTaskInput fileTask;
    CommentType type;
}
