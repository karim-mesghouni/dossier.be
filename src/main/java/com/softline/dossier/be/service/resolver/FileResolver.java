package com.softline.dossier.be.service.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.Document;
import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.domain.FileState;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileResolver implements GraphQLResolver<File> {
    public FileState getCurrentFileState(File file) {
        return Database.querySingle("SELECT fs FROM FileState fs where fs.file.id = :fileId and fs.current = true", FileState.class)
                .setParameter("fileId", file.getId())
                .getSingleResult();
    }

    List<Document> getDocuments(File file) {
        return file.getFileActivities().stream().map(FileActivity::getDocuments).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
