package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.domain.Message;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.SSE.Channel;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.softline.dossier.be.Tools.Functions.safeValue;
import static com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator.can;

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

    @NotNull
    @Override
    public Supplier<Boolean> getPermissionEvaluator(Channel channel) {
        return () -> can("READ_MESSAGE", entity);
    }
}
