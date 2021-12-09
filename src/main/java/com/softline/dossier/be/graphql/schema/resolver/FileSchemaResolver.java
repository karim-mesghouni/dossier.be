package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.domain.FileStateType;
import com.softline.dossier.be.graphql.types.FileFilterInput;
import com.softline.dossier.be.graphql.types.FileHistoryDTO;
import com.softline.dossier.be.graphql.types.PageList;
import com.softline.dossier.be.graphql.types.input.FileInput;
import com.softline.dossier.be.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FileSchemaResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    private final FileService service;

    public File createFile(FileInput input) throws IOException {
        return service.create(input);
    }

    public File updateFile(FileInput input) {
        return service.update(input);
    }

    public List<File> getAllFile() {
        return service.getAll();
    }

    public File getFile(Long id) {
        return service.getById(id);
    }

    public PageList<File> getAllFilePageFilter(FileFilterInput input, int pageNumber, int pageSize) {
        return service.getAllFilesByFilter(input, pageNumber, pageSize);
    }

    public List<FileHistoryDTO> getFileHistory(Long id) {
        return service.getFileHistory(id);
    }

    public List<FileStateType> getAllFileStateType() {
        return service.getAllFileStateType();
    }

    public boolean sendFileToTrash(Long fileId) {
        return service.sendFileToTrash(fileId);
    }

    public boolean recoverFileFromTrash(Long fileId) {
        return service.recoverFileFromTrash(fileId);
    }

    public boolean changeFileOrder(Long fileId, Long fileBeforeId) {
        return service.changeOrder(fileId, fileBeforeId);
    }
}
