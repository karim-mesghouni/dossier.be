package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.domain.FileTask;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.SSE.Channel;
import com.softline.dossier.be.security.domain.Agent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class FileTaskEvent extends EntityEvent<FileTask> {

    public FileTaskEvent(Type type, FileTask fileTask) {
        super("fileTask" + type, fileTask);
        addData("fileTaskId", fileTask.getId());
        addData("fileActivityId", fileTask.getFileActivity().getId());
        addData("fileId", fileTask.getFileActivity().getFile().getId());
    }

    @NotNull
    @Override
    protected Supplier<Boolean> getPermissionEvaluator(Channel channel) throws Exception {
        return () ->  Agent.getByIdentifier(channel.user.getId()).equals(entity.getAssignedTo());
    }
}
