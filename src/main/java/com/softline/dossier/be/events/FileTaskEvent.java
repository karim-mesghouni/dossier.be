package com.softline.dossier.be.events;

import com.softline.dossier.be.domain.FileTask;
import com.softline.dossier.be.events.types.EntityEvent;
import lombok.SneakyThrows;

public class FileTaskEvent extends EntityEvent {
    @SneakyThrows
    public FileTaskEvent(Event type, FileTask fileTask) {
        super("fileTask" + type);
        addData("fileTaskId", fileTask.getId());
        addData("fileActivityId", fileTask.getFileActivity().getId());
        addData("fileId", fileTask.getFileActivity().getFile().getId());
    }
}
