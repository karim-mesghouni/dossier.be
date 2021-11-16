package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.SSE.Channel;
import com.softline.dossier.be.domain.Message;
import com.softline.dossier.be.events.EntityEvent;

import java.util.concurrent.Callable;

import static com.softline.dossier.be.Tools.Functions.safeValue;

public class MessageEvent extends EntityEvent<Message> {

    public MessageEvent(Type type, Message message) {
        super("message" + type, message);
        addData("messageId", message.getId());
        addData("commentId", message.getComment().getId());
        addData("targetId", message.getTargetAgent().getId());
        addData("notifierId", message.getAgent().getId());
        addData("activityId", safeValue(() -> message.getComment().getFileActivity().getId()));
        addData("fileTaskId", safeValue(() -> message.getComment().getFileTask().getId()));
    }

    @Override
    public Callable<Boolean> getPermissionEvaluator(Channel channel) {
        return () -> true;
    }
}
