package com.softline.dossier.be.events;

import com.softline.dossier.be.domain.FileTask;
import com.softline.dossier.be.events.types.EntityEvent;

public class FileTaskEvent extends EntityEvent {

    public FileTaskEvent(Type type, FileTask fileTask) {
        super("fileTask" + type);
        addData("fileTaskId", fileTask.getId());
        addData("fileActivityId", fileTask.getFileActivity().getId());
        addData("fileId", fileTask.getFileActivity().getFile().getId());
    }
}
