package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.domain.ActivityDataField;
import com.softline.dossier.be.domain.ActivityState;
import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.graphql.types.input.ActivityDataFieldInput;
import com.softline.dossier.be.graphql.types.input.FileActivityInput;
import com.softline.dossier.be.service.FileActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FileActivitySchemaResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    private final FileActivityService service;

    public FileActivity createFileActivity(FileActivityInput FileActivity) {
        return service.create(FileActivity);
    }

    public boolean changeDataField(ActivityDataFieldInput input) {
        return service.changeDataField(input);
    }


    protected List<FileActivity> getAllFileActivity() {
        return service.getAll();
    }

    protected FileActivity getFileActivity(Long id) {
        return service.getById(id);
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

    public ActivityDataField getField(long fieldId) {
        return Database.findOrThrow(ActivityDataField.class, fieldId);
    }
}
