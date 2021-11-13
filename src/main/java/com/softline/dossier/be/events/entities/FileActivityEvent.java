package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.events.EntityEvent;

public class FileActivityEvent extends EntityEvent<FileActivity> {

    public FileActivityEvent(Type type, FileActivity fileActivity) {
        super("fileActivity" + type, fileActivity);
        addData("fileActivityId", fileActivity.getId());
        addData("fileId", fileActivity.getFile().getId());
    }
}
