package com.softline.dossier.be.events;

import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.events.types.EntityEvent;

public class FileEvent extends EntityEvent {
    public FileEvent(Event type, File file) {
        super("file" + type);
        addData("fileId", file.getId());
    }
}
