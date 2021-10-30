package com.softline.dossier.be.events;

import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.events.types.EntityEvent;
import lombok.SneakyThrows;

public class FileActivityEvent extends EntityEvent {
    @SneakyThrows
    public FileActivityEvent(Event type, FileActivity fileActivity) {
        super("fileActivity" + type);
        addData("fileActivityId", fileActivity.getId());
        addData("fileId", fileActivity.getFile().getId());
    }
}
