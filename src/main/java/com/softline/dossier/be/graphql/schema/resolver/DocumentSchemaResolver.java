package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.Tools.Database;
import com.softline.dossier.be.domain.Document;
import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.graphql.types.input.DocumentInput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.softline.dossier.be.Tools.Database.database;
import static com.softline.dossier.be.security.domain.Agent.thisDBAgent;

@Component
@PreAuthorize("isAuthenticated()")
@Transactional
public class DocumentSchemaResolver implements GraphQLMutationResolver, GraphQLQueryResolver {

    public Document createDocument(DocumentInput documentInput) {
        return Database.findOrThrow(
                FileActivity.class,
                documentInput.getFileActivity().getId(),
                "CREATE_DOCUMENT_IN_ACTIVITY",
                fileActivity -> Database.persist(Document.builder()
                        .description(documentInput.getDescription())
                        .path(documentInput.getPath())
                        .fileActivity(fileActivity)
                        .agent(thisDBAgent())
                        .build())
        );
    }

    public Document updateDocument(DocumentInput documentInput) {
        return Database.findOrThrow(Document.class, documentInput.getId(), document -> {
            document.setPath(documentInput.getPath());
            document.setDescription(documentInput.getDescription());
        });
    }

    public boolean deleteDocument(long id) {
        return Database.remove(Document.class, id, "DELETE_DOCUMENT");
    }

    public Document getDocument(long id) {
        return Database.findOrThrow(Document.class, id);
    }

    public List<Document> getDocumentsForFile(long fileId) {
        return database().createQuery("SELECT d from Document d where d.fileActivity.file.id = :fileId", Document.class)
                .setParameter("fileId", fileId)
                .getResultList();
    }
}
