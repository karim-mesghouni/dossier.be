package com.softline.dossier.be.graphql.schema.resolver;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.graphql.types.input.ActivityDataFieldInput;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.graphql.types.input.FileTaskInput;
import com.softline.dossier.be.repository.FileTaskRepository;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.service.FileTaskService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.Part;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")

public class FileTaskSchemaResolver extends SchemaResolverBase<FileTask, FileTaskInput, FileTaskRepository, FileTaskService> {


    public FileTask createFileTask(FileTaskInput fileTask) throws IOException {
        return create(fileTask);
    }

    public FileTask updateFileTask(FileTaskInput fileTask) {
        return update(fileTask);
    }

    public boolean deleteFileTask(Long id) {
        return delete(id);
    }

    public List<FileTask> getAllFileTask() {
        return getAll();
    }

    public FileTask getFileTask(Long id) {
        return get(id);
    }

    public List<TaskSituation> getAllTaskSituations(Long taskId) {
        return service.getAllTaskSituations(taskId);
    }

    public Agent changeAssignedTo(Long assignedToId, Long fileTaskId) {
        return service.changeAssignedTo(assignedToId, fileTaskId);
    }

    public Agent changeReporter(Long reporterId, Long fileTaskId) {
        return service.changeReporter(reporterId, fileTaskId);

    }

    public FileTaskSituation changeFileTaskSituation(Long situationId, Long fileTaskId) {
        return service.changeFileTaskSituation(situationId, fileTaskId);

    }

    public List<FileTask> getAllFileTaskByFileActivityId(Long fileActivityId) {
        return service.getAllFileTaskByFileActivityId(fileActivityId);

    }

    public List<FileTask> getAllFileTaskByFileActivityIdInTrash(Long fileActivityId) {
        return service.getAllFileTaskByFileActivityIdInTrash(fileActivityId);

    }

    public List<FileTask> getAllFileTaskByAssignedToId(Long assignedToId) {
        return service.getAllFileTaskByAssignedToId(assignedToId);
    }

    public boolean changeToStartDate(LocalDateTime toStartDate, Long fileTaskId) {
        return service.changeToStartDate(toStartDate, fileTaskId);
    }

    public boolean changeDueDate(LocalDateTime dueDate, Long fileTaskId) {

        return service.changeDueDate(dueDate, fileTaskId);
    }

    public boolean changeTitle(String title, Long fileTaskId) {
        return service.changeTitle(title, fileTaskId);
    }

    public ReturnedComment changeRetour(CommentInput input) {
        return service.changeRetour(input);
    }

    public DescriptionComment changeDescription(CommentInput input) {
        return service.changeDescription(input);
    }

    public boolean changeDataField(ActivityDataFieldInput input) {
        return service.changeDataField(input);
    }

    public List<ReturnedCause> getAllReturnedCause() {
        return service.getAllReturnedCause();
    }

    public ReturnedCause changeReturnedCause(Long fileTaskId, Long returnedCauseId) {
        return service.changeReturnedCause(fileTaskId, returnedCauseId);
    }

    public boolean changeReturned(Long fileTaskId, boolean returned) {
        return service.changeReturned(fileTaskId, returned);
    }

    public TaskState changeState(Long fileTaskId, Long taskStateId) {
        return service.changeState(fileTaskId, taskStateId);
    }

    public FileTask createChildFileTask(FileTaskInput fileTask) {
        return service.createChildFileTask(fileTask);
    }

    public FileTask changeParent(Long FileTaskId, Long parentId) {
        return service.changeParent(FileTaskId, parentId);
    }

    public boolean recoverFileTaskFromTrash(Long fileTaskId) {
        return service.recoverFileTaskFromTrash(fileTaskId);
    }

    public boolean sendFileTaskToTrash(Long fileTaskId) {
        return service.sendFileTaskToTrash(fileTaskId);
    }

    public List<Attachment> uploadAttachments(List<Part> part, Long fileTaskId, DataFetchingEnvironment environment) throws IOException, NoSuchAlgorithmException {
        return service.saveAttached(fileTaskId, environment);
    }

    public List<Attachment> getAttachedFile(Long idFileTAsk) {
        return null;
    }

    public boolean fileTaskOrderUp(Long fileTaskId) {
        return service.fileTaskOrderUp(fileTaskId);
    }

    public boolean fileTaskOrderDown(Long fileTaskId) {
        return service.fileTaskOrderDown(fileTaskId);
    }
}
