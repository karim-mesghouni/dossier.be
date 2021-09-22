package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.FileDoc;
import com.softline.dossier.be.graphql.types.input.FileDocInput;
import com.softline.dossier.be.repository.FileDocRepository;
import com.softline.dossier.be.service.FileDocService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class FileDocSchemaResolver extends SchemaResolverBase<FileDoc, FileDocInput, FileDocRepository, FileDocService> {


    public FileDoc createFileDoc(FileDocInput input) throws IOException {
        return create(input);
    }

    public FileDoc updateFileDoc(FileDocInput input) {
        return update(input);
    }

    public boolean deleteFileDoc(Long id) {
        return delete(id);
    }

    public List<FileDoc> getAllFileDoc() {
        return getAll();
    }

    public FileDoc getFileDoc(Long id) {
        return get(id);
    }

    public List<FileDoc> getAllByFileActivityIdOrFileId(Long fileActivityId, Long fileId) throws Exception {
        return service.getAllByFileActivityIdOrFileId(fileActivityId, fileId);
    }
}
