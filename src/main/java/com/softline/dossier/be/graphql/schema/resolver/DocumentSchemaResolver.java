package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Document;
import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.DocumentEvent;
import com.softline.dossier.be.graphql.types.input.DocumentInput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.softline.dossier.be.security.domain.Agent.thisDBAgent;

@Component
@PreAuthorize("isAuthenticated()")
public class DocumentSchemaResolver implements GraphQLMutationResolver, GraphQLQueryResolver {

    public Document getDocument(Long id) {
        return Database.findOrThrow(Document.class, id);
    }

    public List<Document> getDocumentsForFile(long fileId) {
        return Database.query("SELECT d from Document d where d.fileActivity.file.id = :fileId", Document.class)
                .setParameter("fileId", fileId)
                .getResultList();
    }

    public Document createDocument(DocumentInput documentInput) {
        return Database.findOrThrow(FileActivity.class,
                documentInput.getFileActivity(),
                "CREATE_DOCUMENT_IN_ACTIVITY",
                fileActivity -> {
                    Database.startTransaction();
                    var doc = Database.persist(Document.builder()
                            .description(documentInput.getDescription())
                            .path(documentInput.getPath())
                            .fileActivity(fileActivity)
                            .agent(thisDBAgent())
                            .build());
                    Database.commit();
                    new DocumentEvent(EntityEvent.Type.ADDED, doc).fireToAll();
                    return doc;
                }
        );
    }

    public Document updateDocument(DocumentInput documentInput) {
        var d = Database.findOrThrow(Document.class, documentInput.getId(), "UPDATE_DOCUMENT");
        Database.startTransaction();
        d.setPath(documentInput.getPath());
        d.setDescription(documentInput.getDescription());
        Database.commit();
        new DocumentEvent(EntityEvent.Type.UPDATED, d).fireToAll();
        return d;
    }

    public boolean deleteDocument(Long id) {
        return Database.afterRemoving(Document.class, id, "DELETE_DOCUMENT", doc -> new DocumentEvent(EntityEvent.Type.DELETED, doc).fireToAll());
    }


}
