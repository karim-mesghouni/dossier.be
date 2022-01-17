package com.softline.dossier.be.graphql.schema.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.graphql.types.input.FileTaskInput;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.service.FileTaskService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.Part;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FileTaskSchemaResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    private final FileTaskService service;

    public FileTask createFileTask(FileTaskInput fileTask) {
        return service.create(fileTask);
    }

    public FileTask updateFileTask(FileTaskInput fileTask) {
        return service.update(fileTask);
    }

    public List<FileTask> getAllFileTask() {
        return getAll();
    }

    public FileTask getFileTask(long fileTaskId) {
        return service.getById(fileTaskId);
    }

    public List<TaskSituation> getAllTaskSituations(long taskId) {
        return service.getAllTaskSituations(taskId);
    }

    public Agent changeAssignedTo(long assignedToId, long fileTaskId) {
        return service.changeAssignedTo(assignedToId, fileTaskId);
    }

    public FileTaskSituation changeFileTaskSituation(long situationId, long fileTaskId) {
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

    public boolean changeStartDate(LocalDateTime startDate, Long fileTaskId) {
        return service.changeStartDate(startDate, fileTaskId);
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

    public boolean changeParent(Long FileTaskId, Long parentId) {
        return service.changeParent(FileTaskId, parentId);
    }

    public boolean recoverFileTaskFromTrash(Long fileTaskId) {
        return service.recoverFileTaskFromTrash(fileTaskId);
    }

    public boolean sendFileTaskToTrash(Long fileTaskId) {
        return service.sendFileTaskToTrash(fileTaskId);
    }

    public List<FileTaskAttachment> uploadAttachments(List<Part> part, Long fileTaskId, DataFetchingEnvironment environment) throws IOException {
        return service.saveAttached(fileTaskId, environment);
    }

    public List<FileTask> getAll() {
        return service.getAll();
    }

    public boolean changeFileTaskOrder(long fileTaskId, long fileTaskBeforeId) {
        return service.changeOrder(fileTaskId, fileTaskBeforeId);
    }

    public CheckSheet setCheckSheet(Part file, long fileTaskId, DataFetchingEnvironment environment) {
        return service.setCheckSheet(fileTaskId, environment);
    }

    public void removeCheckSheet(Long checkSheetId) {
        service.removeCheckSheet(checkSheetId);
    }
}
