package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.domain.Message;
import com.softline.dossier.be.events.EntityEvent;

import javax.persistence.EntityManager;

import static com.softline.dossier.be.Application.context;
import static com.softline.dossier.be.Tools.Functions.safeValue;

public class TaskEvent extends EntityEvent {

    public TaskEvent(Type type, Message message) {
        super("task" + type);
        var em = context().getBean(EntityManager.class);
        var file = em.find(File.class, 1);
        addData("messageId", message.getId());
        addData("commentId", message.getComment().getId());
        addData("targetId", message.getTargetAgent().getId());
        addData("notifierId", message.getAgent().getId());
        addData("activityId", safeValue(() -> message.getComment().getFileActivity().getId(), null));
        addData("fileTaskId", safeValue(() -> message.getComment().getFileTask().getId(), null));
    }
}
