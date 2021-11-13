package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.domain.Comment;
import com.softline.dossier.be.events.EntityEvent;

import static com.softline.dossier.be.Tools.Functions.safeValue;

public class CommentEvent extends EntityEvent<Comment> {

    public CommentEvent(Type type, Comment comment) {
        super("comment" + type, comment);
        addData("commentId", comment.getId());
        addData("fileActivityId", safeValue(() -> comment.getFileActivity().getId()));
        addData("fileTaskId", safeValue(() -> comment.getFileTask().getId()));
    }
}
