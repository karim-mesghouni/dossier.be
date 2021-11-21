package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.ActivityState;
import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.graphql.types.input.ActivityDataFieldInput;
import com.softline.dossier.be.graphql.types.input.FileActivityInput;
import com.softline.dossier.be.repository.FileActivityRepository;
import com.softline.dossier.be.service.FileActivityService;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FileActivitySchemaResolver extends SchemaResolverBase<FileActivity, FileActivityInput, FileActivityRepository, FileActivityService> {

    public FileActivity createFileActivity(FileActivityInput FileActivity) throws IOException, ClientReadableException {
        return create(FileActivity);
    }

    public boolean changeDataField(ActivityDataFieldInput input) throws ClientReadableException {
        return service.changeDataField(input);
    }

    public FileActivity updateFileActivity(FileActivityInput FileActivity) throws ClientReadableException {
        return update(FileActivity);
    }

    public boolean deleteFileActivity(Long id) throws ClientReadableException {
        return delete(id);
    }

    protected List<FileActivity> getAllFileActivity() {
        return getAll();
    }

    protected FileActivity getFileActivity(Long id) {
        return getService().getById(id);
    }

    public List<FileActivity> getAllFileActivityByFileId(Long fileId) {
        return service.getAllFileActivityByFileId(fileId);
    }

    public List<FileActivity> getAllFileActivityByFileIdInTrash(Long fileId) {
        return service.getAllFileActivityByFileIdInTrash(fileId);
    }

    public ActivityState changeActivityState(Long activityStateId, Long fileActivityId) {
        return service.changeActivityState(activityStateId, fileActivityId);
    }

    public boolean recoverFileActivityFromTrash(Long fileActivityId) {
        return service.recoverFileActivityFromTrash(fileActivityId);
    }

    public boolean sendFileActivityToTrash(Long fileActivityId) {
        return service.sendFileActivityToTrash(fileActivityId);
    }

    public boolean changeFileActivityOrder(long fileActivityId, long fileActivityBeforeId) {
        return service.changeOrder(fileActivityId, fileActivityBeforeId);
    }
}
