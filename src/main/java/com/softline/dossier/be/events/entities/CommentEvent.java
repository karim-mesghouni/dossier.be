package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.domain.Comment;
import com.softline.dossier.be.events.EntityEvent;

import static com.softline.dossier.be.Tools.Functions.safeValue;

public class CommentEvent extends EntityEvent {

    public CommentEvent(Type type, Comment comment) {
        super("comment" + type);
        addData("commentId", comment.getId());
        addData("fileActivityId", safeValue(() -> comment.getFileActivity().getId(), null));
        addData("fileTaskId", safeValue(() -> comment.getFileTask().getId(), null));
    }
}
