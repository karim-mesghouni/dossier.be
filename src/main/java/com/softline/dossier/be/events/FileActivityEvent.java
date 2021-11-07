package com.softline.dossier.be.events;

import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.events.types.EntityEvent;

public class FileActivityEvent extends EntityEvent {

    public FileActivityEvent(Event type, FileActivity fileActivity) {
        super("fileActivity" + type);
        addData("fileActivityId", fileActivity.getId());
        addData("fileId", fileActivity.getFile().getId());
    }
}
