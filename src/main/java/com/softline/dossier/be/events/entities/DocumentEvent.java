package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.domain.Document;
import com.softline.dossier.be.events.EntityEvent;

public class DocumentEvent extends EntityEvent<Document> {
    public DocumentEvent(Type type, Document document) {
        super("document" + type, document);
        addData("documentId", document.getId());
        addData("fileId", document.getFileActivity().getFile().getId());
    }
}
