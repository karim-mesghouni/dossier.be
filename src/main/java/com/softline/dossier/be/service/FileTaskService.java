package com.softline.dossier.be.service;

import com.softline.dossier.be.SSE.EventController;
import com.softline.dossier.be.Tools.Database;
import com.softline.dossier.be.Tools.FileSystem;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.CommentType;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.graphql.types.input.FileTaskInput;
import com.softline.dossier.be.repository.*;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.security.repository.AgentRepository;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.io.FilenameUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.softline.dossier.be.Tools.Database.database;
import static com.softline.dossier.be.Tools.Functions.*;
import static com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator.cannot;
import static com.softline.dossier.be.security.domain.Agent.thisDBAgent;

@Transactional
@Service
@RequiredArgsConstructor
public class FileTaskService extends IServiceBase<FileTask, FileTaskInput, FileTaskRepository> {
    private final TaskRepository taskRepository;
    private final FileTaskSituationRepository fileTaskSituationRepository;
    private final TaskSituationRepository taskSituationRepository;
    private final AgentRepository agentRepository;
    private final TaskStateRepository taskStateRepository;
    private final DescriptionCommentRepository descriptionCommentRepository;
    private final ReturnedCommentRepository returnedCommentRepository;
    private final FileActivityRepository fileActivityRepository;
    private final ReturnedCauseRepository returnedCauseRepository;
    private final FileTaskAttachmentRepository fileTaskAttachmentRepository;


    @Override
    public List<FileTask> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public FileTask create(FileTaskInput input) {
        var reporter = (Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var task = taskRepository.findById(input.getTask().getId()).orElseThrow();
        var fileActivity = fileActivityRepository.findById(input.getFileActivity().getId()).orElseThrow();
        var count = getRepository().countFileTaskByFileActivity_File_Id(fileActivity.getFile().getId());
        File file = fileActivity.getFile();
        var fileTask = FileTask.builder()
                .fileActivity(fileActivity)
                .task(task)
                .toStartDate(LocalDateTime.now())
                .order((count + 1))
                .fileTaskSituations(new ArrayList<>())
                .reporter(reporter)
                .number(file.getNextFileTaskNumber())
                .build();
        var fileTaskSituation = FileTaskSituation.builder()
                .situation(task.getSituations().stream().filter(TaskSituation::isInitial).findFirst().orElseThrow())
                .fileTask(fileTask)
                .current(true)
                .build();
        fileTask.getFileTaskSituations().add(fileTaskSituation);
        file.incrementNextFileTaskNumber();
        return getRepository().save(fileTask);
    }

    @Override
    public FileTask update(FileTaskInput input) {
        var fileTask = getRepository().findById(input.getId()).orElseThrow();
        fileTask.setToStartDate(input.getToStartDate());
        fileTask.setDueDate(input.getDueDate());
        //fileTask.setDescription(input.getDescription());
        // fileTask.setRetour(input.getRetour());
        fileTask.setTitle(input.getTitle());
        safeRun(() -> fileTask.setState(taskStateRepository.findById(input.getState().getId()).orElseThrow()));
        return fileTask;
    }

    @Override
    public boolean delete(long id) {
        return false;
    }

    @Override
    public FileTask getById(long id) {
        return repository.findById(id).orElseThrow();
    }

    public FileTaskSituation getCurrentFilePhaseState(Long fileAgentId) {
        return null; //fileTaskStateRepository.getFilePhaseStateByPhaseAgent_IdAndCurrentIsTrue(fileAgentId);
    }

    public List<TaskSituation> getAllTaskSituations(Long taskId) {
        return taskSituationRepository.findAllByTask_Id(taskId);
    }

    @PreAuthorize("hasPermission(#fileTaskId, 'FileTask', 'UPDATE_FILE_TASK')")
    public Agent changeAssignedTo(Long assignedToId, Long fileTaskId) {
        var assigned = agentRepository.findById(assignedToId).orElseThrow();
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setAssignedTo(assigned);
        getRepository().save(fileTask);
        return assigned;
    }

    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public Agent changeReporter(Long reporterId, Long fileTaskId) {
        var reporter = agentRepository.findById(reporterId).orElseThrow();
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setReporter(reporter);
        getRepository().save(fileTask);
        return reporter;
    }

    @PreAuthorize("hasPermission(#fileTaskId, 'FileTask', 'WORK_IN_FILE_TASK') or hasPermission(#fileTaskId, 'FileTask', 'UPDATE_FILE_TASK')")
    public FileTaskSituation changeFileTaskSituation(Long situationId, Long fileTaskId) throws ClientReadableException {
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        var situation = taskSituationRepository.findById(situationId).orElseThrow();
        if (fileTask.getCurrentState().getSituation().isBlock()) {
            throw new ClientReadableException("vous ne pouvez pas modifier le statut d'une tâche bloquée, veuillez débloquer la tâche et réessayer.");
        }
        var oldSituation = fileTaskSituationRepository.findFirstByFileTaskAndCurrentIsTrue(fileTask);
        if (oldSituation != null) {
            if (oldSituation.getSituation().getId() == situationId) {
                return oldSituation;
            }
            oldSituation.setCurrent(false);
        }
        if (situation.isFinal()) {
            fileTask.setEndDate(LocalDateTime.now());
        } else {
            if (!situation.isFinal() && !situation.isInitial()) {
                fileTask.setStartDate(LocalDateTime.now());
            }
        }

        return fileTaskSituationRepository.save(FileTaskSituation.builder()
                .situation(situation)
                .current(true)
                .fileTask(fileTask).build());
    }

    public List<FileTask> getAllFileTaskByFileActivityId(Long fileActivityId) {

        return getRepository().findAllByFileActivity_Id(fileActivityId);
    }

    public List<FileTask> getAllFileTaskByAssignedToId(Long assignedToId) {
        return getRepository().findAllByAssignedTo_Id(assignedToId);
    }

    @PreAuthorize("hasPermission(#fileTaskId, 'FileTask', 'UPDATE_FILE_TASK')")
    public boolean changeToStartDate(LocalDateTime toStartDate, Long fileTaskId) {
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setToStartDate(toStartDate);
        return true;
    }

    @PreAuthorize("hasPermission(#fileTaskId, 'FileTask', 'UPDATE_FILE_TASK')")
    public boolean changeDueDate(LocalDateTime dueDate, Long fileTaskId) {
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setDueDate(dueDate);
        return true;
    }

    // permission inside
    public boolean changeTitle(String title, Long fileTaskId) {
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        if (cannot("UPDATE_FILE_TASK", fileTask) && cannot("UPDATE_FILE_ACTIVITY", fileTask.getFileActivity())) {
            throw new AccessDeniedException("erreur de privilege");
        }
        fileTask.setTitle(title);
        return true;
    }

    @Transactional
    @PreAuthorize("hasPermission(#commentInput.fileTask.id, 'FileTask', 'UPDATE_FILE_TASK')")
    public DescriptionComment changeDescription(CommentInput commentInput) {
        var fileTask = getRepository().findById(commentInput.getFileTask().getId()).orElseThrow();
        var description = fileTask.getDescription();
        if (description == null) {
            description = DescriptionComment.builder()
                    .fileTask(fileTask)
                    .agent(thisDBAgent())
                    .type(CommentType.Description)
                    .fileActivity(fileTask.getFileActivity())
                    .createdDate(LocalDateTime.now())
                    .build();
            fileTask.setDescription(description);
        }
        description.setContent(commentInput.getContent());
        descriptionCommentRepository.save(description);
        return description;
    }

    // permission inside
    public ReturnedComment changeRetour(CommentInput retour) {
        var fileTask = getRepository().findById(retour.getFileTask().getId()).orElseThrow();
        if (cannot("WORK_IN_FILE_TASK", fileTask) && cannot("UPDATE_FILE_ACTIVITY", fileTask.getFileActivity())) {
            throw new AccessDeniedException("erreur de privilege");
        }
        if (retour.getId() != null) {
            var retourExist = returnedCommentRepository.findById(retour.getId()).orElseThrow();
            retourExist.setContent(retour.getContent());
            return retourExist;
        } else {
            var fileActivity = fileActivityRepository.findById(retour.getFileActivity().getId()).orElseThrow();

            var retourNew = returnedCommentRepository.save(ReturnedComment.builder()
                    .fileActivity(fileActivity)
                    .content(retour.getContent())
                    .fileTask(fileTask)
                    .agent(thisDBAgent())
                    .build()
            );
            fileTask.setRetour(retourNew);
            retourNew.setType(CommentType.Returned);
            return retourNew;
        }
    }


    public List<ReturnedCause> getAllReturnedCause() {
        return returnedCauseRepository.findAll();
    }

    // permission inside
    public ReturnedCause changeReturnedCause(Long fileTaskId, Long returnedCauseId) {
        var returnedCause = returnedCauseRepository.findById(returnedCauseId).orElseThrow();
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        if (cannot("WORK_IN_FILE_TASK", fileTask) && cannot("UPDATE_FILE_ACTIVITY", fileTask.getFileActivity())) {
            throw new AccessDeniedException("erreur de privilege");
        }
        fileTask.setReturnedCause(returnedCause);
        getRepository().save(fileTask);
        return returnedCause;
    }

    // permission inside
    public TaskState changeState(Long fileTaskId, Long taskStateId) {
        var taskState = taskStateRepository.findById(taskStateId).orElseThrow();
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        if (cannot("WORK_IN_FILE_TASK", fileTask) && cannot("UPDATE_FILE_ACTIVITY", fileTask.getFileActivity())) {
            throw new AccessDeniedException("erreur de privilege");
        }
        fileTask.setState(taskState);
        getRepository().save(fileTask);
        return taskState;
    }

    // permission inside
    public boolean changeReturned(Long fileTaskId, boolean returned) {
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        if (cannot("WORK_IN_FILE_TASK", fileTask) && cannot("UPDATE_FILE_ACTIVITY", fileTask.getFileActivity())) {
            throw new AccessDeniedException("erreur de privilege");
        }
        fileTask.setReturned(returned);
        getRepository().save(fileTask);
        return returned;
    }

    // permission inside
    public FileTask createChildFileTask(FileTaskInput input) {
        if (cannot("CREATE_FILE_TASK", input)) {
            throw new AccessDeniedException("erreur de privilege");
        }
        var reporter = agentRepository.findByUsername(((Agent) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        var parent = getRepository().findById(input.getParent().getId()).orElseThrow();
        var task = taskRepository.findById(input.getTask().getId()).orElseThrow();
        var fileActivity = fileActivityRepository.findById(input.getFileActivity().getId()).orElseThrow();
        var count = getRepository().countFileTaskByFileActivity_File_Id(fileActivity.getFile().getId());
        var fileTask = FileTask.builder()
                .fileActivity(fileActivity)
                .task(task)
                .toStartDate(LocalDateTime.now())
                .order((count + 1))
                .parent(parent)
                .reporter(reporter)
                .returned(true)
                .fileTaskSituations(new ArrayList<>())
                .build();
        var fileTaskSituation = FileTaskSituation.builder()
                .situation(task.getSituations().stream().filter(TaskSituation::isInitial).findFirst().orElseThrow())
                .fileTask(fileTask)
                .current(true)
                .build();
        fileTask.getFileTaskSituations().add(fileTaskSituation);
        getRepository().save(fileTask);
        return getRepository().findById(fileTask.getId()).orElseThrow();
    }

    @PreAuthorize("hasPermission(#fileTaskId, 'FileTask', 'UPDATE_FILE_TASK')")
    public boolean changeParent(Long fileTaskId, Long parentId) {
        return safeRunWithFallback(
                () -> getRepository().findById(fileTaskId).orElseThrow().setParent(getRepository().findById(parentId).orElseThrow()),
                () -> getRepository().findById(fileTaskId).orElseThrow().setParent(null)
        );
    }

    public List<FileTask> getAllFileTaskByFileActivityIdInTrash(Long fileActivityId) {
        return getRepository().findAllByFileActivity_Id_In_Trash(fileActivityId);
    }

    public boolean recoverFileTaskFromTrash(Long fileTaskId) {
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setInTrash(false);
        return true;
    }

    public boolean sendFileTaskToTrash(Long fileTaskId) {
        return tap(true, $true -> database().find(FileTask.class, fileTaskId).setInTrash($true));
    }

    public List<Attachment> saveAttached(Long fileTaskId, DataFetchingEnvironment environment) throws IOException {
        ArrayList<ApplicationPart> files = environment.getArgument("attachments");
        var filesAttached = new ArrayList<FileTaskAttachment>();
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        for (ApplicationPart file : files) {
            String originalName = file.getSubmittedFileName();
            String storageName = FileSystem.randomMD5() + "." + FilenameUtils.getExtension(originalName);
            Path newPath = FileSystem.getAttachmentsPath().resolve(storageName);
            Files.copy(file.getInputStream(), newPath);
            // remove the temp file
            file.delete();
            filesAttached.add(FileTaskAttachment.builder()
                    .storageName(storageName)
                    .realName(originalName)
                    .contentType(file.getContentType())
                    .fileTask(fileTask)
                    .build());
            fileTaskAttachmentRepository.saveAll(filesAttached);
        }

        return filesAttached.stream().map(e -> (Attachment) e).collect(Collectors.toList());
    }

    public boolean deleteAttached(Long attachedId) {
        var attached = fileTaskAttachmentRepository.findById(attachedId).orElseThrow();
        try {
            Files.deleteIfExists(FileSystem.getAttachmentsPath().resolve(attached.getStorageName()));
            fileTaskAttachmentRepository.delete(attached);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean delete(Long id) {
        return Database.remove(FileTask.class, id, "DELETE_FILE_TASK");
    }

    public boolean updateTitle(String title, long fileTaskId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, "UPDATE_FILE_TASK", fileTask -> {
            fileTask.setTitle(title);
            return true;
        });
    }

    /**
     * change the order of a fileTask,
     * will be called when the user changes the order of a fileTask in the FilesView
     * in the case when fileTaskBeforeId is not existent the fileTask will be moved to be the first item in the list
     *
     * @param fileTaskId       the fileTask(id) that we want to change its order
     * @param fileTaskBeforeId the fileTask(id) which should be before the new position of the fileTask, may be non-existent
     * @return boolean
     */
    @Transactional
    public synchronized boolean changeOrder(long fileTaskId, long fileTaskBeforeId) {
        EventController.silently(() -> {
            if (repository.count() < 2) return;// this should not happen
            var fileTask = repository.findById(fileTaskId).orElseThrow();
            var fileActivityId = fileTask.getFileActivity().getId();
            var res = repository.findById(fileTaskBeforeId);
            // TODO: convert this logic into Mutating queries in JPA
            if (res.isPresent()) {
                var fileTaskBefore = res.get();
                // how many fileTasks will be updated (increment or decrement their order)
                var levelsChange = repository.countAllByOrderBetween(fileTask.getOrder(), fileTaskBefore.getOrder(), fileActivityId);
                if (fileTask.getOrder() < fileTaskBefore.getOrder()) {// fileTask is moving down the list
                    repository.findAllByOrderAfter(fileTask.getOrder(), fileActivityId)
                            .stream()
                            .limit(levelsChange + 1)
                            .forEach(FileTask::decrementOrder);
                    fileTask.setOrder(fileTaskBefore.getOrder() + 1);
                } else {// fileTask is moving up the list
                    var allAfter = repository.findAllByOrderAfter(fileTaskBefore.getOrder(), fileActivityId);
                    allAfter.stream()
                            .limit(levelsChange)
                            .forEach(FileTask::incrementOrder);
                    fileTask.setOrder(repository.findAllByOrderAfter(fileTaskBefore.getOrder(), fileActivityId).stream().findFirst().get().getOrder() - 1);
                }
            } else {// fileTask should be the first item in the list
                repository.findAllByOrderBefore(fileTask.getOrder(), fileActivityId).forEach(FileTask::incrementOrder);
                fileTask.setOrder(repository.minOrder(fileActivityId) - 1);
            }
        });
        return true;
    }
}