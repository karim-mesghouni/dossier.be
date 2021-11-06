package com.softline.dossier.be.events;

import com.softline.dossier.be.domain.Message;
import com.softline.dossier.be.events.types.EntityEvent;
import lombok.SneakyThrows;

import static com.softline.dossier.be.Halpers.Functions.safeValue;

public class MessageEvent extends EntityEvent {
    @SneakyThrows
    public MessageEvent(Event type, Message message) {
        super("message" + type);
        addData("messageId", message.getId());
        addData("commentId", message.getComment().getId());
        addData("targetId", message.getTargetAgent().getId());
        addData("notifierId", message.getAgent().getId());
        addData("activityId", safeValue(() -> message.getComment().getFileActivity().getId(), null));
        addData("fileTaskId", safeValue(() -> message.getComment().getFileTask().getId(), null));
    }
}
