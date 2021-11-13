package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.events.EntityEvent;

public class FileEvent extends EntityEvent<File> {
    public FileEvent(Type type, File file) {
        super("file" + type, file);
        addData("fileId", file.getId());
        addData("baseActivityId", file.getBaseActivity().getId());
    }
}
