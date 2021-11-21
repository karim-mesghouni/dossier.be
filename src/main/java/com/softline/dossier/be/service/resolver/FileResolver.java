package com.softline.dossier.be.service.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.domain.FileState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileResolver implements GraphQLResolver<File> {
    public FileState getCurrentFileState(File file) {
        return Database.query("SELECT fs FROM FileState fs where fs.file.id = :fileId and fs.current = true", FileState.class)
                .setParameter("fileId", file.getId())
                .setMaxResults(1)
                .getSingleResult();
    }

}
