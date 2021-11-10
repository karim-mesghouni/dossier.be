package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.domain.Message;
import com.softline.dossier.be.events.EntityEvent;

import static com.softline.dossier.be.Tools.Functions.safeValue;

public class MessageEvent extends EntityEvent {

    public MessageEvent(Type type, Message message) {
        super("message" + type);
        addData("messageId", message.getId());
        addData("commentId", message.getComment().getId());
        addData("targetId", message.getTargetAgent().getId());
        addData("notifierId", message.getAgent().getId());
        addData("activityId", safeValue(() -> message.getComment().getFileActivity().getId()));
        addData("fileTaskId", safeValue(() -> message.getComment().getFileTask().getId()));
    }
}
