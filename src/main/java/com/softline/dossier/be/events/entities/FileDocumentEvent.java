package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.domain.FileDoc;
import com.softline.dossier.be.events.EntityEvent;

import static com.softline.dossier.be.Tools.Functions.safeValue;

public class FileDocumentEvent extends EntityEvent<FileDoc> {
    public FileDocumentEvent(Type type, FileDoc document) {
        super("fileDocument" + type, document);
        addData("fileDocumentId", document.getId());
        addData("fileId", safeValue(() -> document.getFile().getId()));
        addData("fileActivityId", safeValue(() -> document.getFileActivity().getId()));
    }
}
