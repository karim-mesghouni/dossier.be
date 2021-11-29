package com.softline.dossier.be.service;

import com.softline.dossier.be.Tools.FileSystem;
import com.softline.dossier.be.Tools.TipTap;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.database.OrderManager;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.CommentType;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.CommentEvent;
import com.softline.dossier.be.events.entities.FileTaskEvent;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.graphql.types.input.FileTaskInput;
import com.softline.dossier.be.repository.FileTaskAttachmentRepository;
import com.softline.dossier.be.repository.FileTaskRepository;
import com.softline.dossier.be.repository.FileTaskSituationRepository;
import com.softline.dossier.be.security.domain.Agent;
import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.softline.dossier.be.Tools.Functions.safeRun;
import static com.softline.dossier.be.Tools.Functions.safeRunWithFallback;
import static com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator.DenyOrProceed;
import static com.softline.dossier.be.security.domain.Agent.thisDBAgent;

@Service
@RequiredArgsConstructor
public class FileTaskService {
    private final FileTaskSituationRepository fileTaskSituationRepository;
    private final FileTaskAttachmentRepository fileTaskAttachmentRepository;
    private final FileTaskRepository repository;

    public FileTask getById(long id) {
        return Database.findOrThrow(FileTask.class, id);
    }

    public List<FileTask> getAll() {
        return Database.findAll(FileTask.class);
    }

    public List<TaskSituation> getAllTaskSituations(Long taskId) {
        return Database.query("SELECT tsi FROM TaskSituation tsi where tsi.task.id = :taskId", TaskSituation.class)
                .setParameter("taskId", taskId)
                .getResultList();
    }

    public List<ReturnedCause> getAllReturnedCause() {
        return Database.findAll(ReturnedCause.class);
    }


    public FileTask create(FileTaskInput input) {
        var task = Database.findOrThrow(Task.class, input.getTask());
        var fileActivity = Database.findOrThrow(FileActivity.class, input.getFileActivity());
        var count = repository.countFileTaskByFileActivity_File_Id(fileActivity.getFile().getId());
        File file = fileActivity.getFile();
        var fileTask = FileTask.builder()
                .fileActivity(fileActivity)
                .task(task)
                .toStartDate(LocalDateTime.now())
                .order((count + 1))
                .fileTaskSituations(new ArrayList<>())
                .reporter(Agent.thisAgent())
                .number(file.getNextFileTaskNumber())
                .build();
        var fileTaskSituation = FileTaskSituation.builder()
                .situation(task.getSituations().stream().filter(TaskSituation::isInitial).findFirst().orElseThrow())
                .fileTask(fileTask)
                .current(true)
                .build();
        Database.startTransaction();
        fileTask.getFileTaskSituations().add(fileTaskSituation);
        file.incrementNextFileTaskNumber();
        Database.persist(fileTask);
        Database.commit();
        new FileTaskEvent(EntityEvent.Type.ADDED, fileTask).fireToAll();
        return fileTask;
    }

    public FileTask update(FileTaskInput input) {
        var fileTask = Database.findOrThrow(FileTask.class, input);
        DenyOrProceed("UPDATE_FILE_TASK", fileTask);
        Database.startTransaction();
        fileTask.setToStartDate(input.getToStartDate());
        fileTask.setDueDate(input.getDueDate());
        fileTask.setTitle(input.getTitle());
        safeRun(() -> fileTask.setState(Database.findOrThrow(TaskState.class, input.getState())));
        Database.commit();
        new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
        return fileTask;
    }


    public FileTaskSituation getCurrentFilePhaseState(Long fileAgentId) {
        return null; //fileTaskStateRepository.getFilePhaseStateByPhaseAgent_IdAndCurrentIsTrue(fileAgentId);
    }


    public Agent changeAssignedTo(long assignedToId, long fileTaskId) {
        var fileTask = Database.findOrThrow(FileTask.class, fileTaskId);
        DenyOrProceed("UPDATE_FILE_TASK", fileTask);
        var assigned = Database.findOrNull(Agent.class, assignedToId);
        Database.startTransaction();
        fileTask.setAssignedTo(assigned);
        Database.commit();
        new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
        return assigned;
    }

    public Agent changeReporter(long reporterId, long fileTaskId) {
        var fileTask = Database.findOrThrow(FileTask.class, fileTaskId);
        DenyOrProceed("ADMIN", fileTask);
        var reporter = Database.findOrNull(Agent.class, reporterId);
        Database.startTransaction();
        fileTask.setReporter(reporter);
        Database.commit();
        new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
        return reporter;
    }

    public FileTaskSituation changeFileTaskSituation(Long situationId, Long fileTaskId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, fileTask -> {
            DenyOrProceed("WORK_IN_FILE_TASK", fileTask);
            Database.startTransaction();
            var situation = Database.findOrThrow(TaskSituation.class, situationId);
            if (fileTask.getCurrentState().getSituation().isBlock()) {
                throw new GraphQLException("vous ne pouvez pas modifier le statut d'une tâche bloquée, veuillez débloquer la tâche et réessayer.");
            }
            var oldSituation = Database.findOrThrow(fileTaskSituationRepository.findFirstByFileTaskAndCurrentIsTrue(fileTask));
            if (oldSituation.getSituation().getId() == situationId) {
                return oldSituation;
            }
            oldSituation.setCurrent(false);
            if (situation.isFinal()) {
                fileTask.setEndDate(LocalDateTime.now());
            } else {
                if (!situation.isFinal() && !situation.isInitial()) {
                    fileTask.setStartDate(LocalDateTime.now());
                }
            }

            var state = Database.persist(FileTaskSituation.builder()
                    .situation(situation)
                    .current(true)
                    .fileTask(fileTask).build());
            Database.commit();
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
            return state;
        });
    }

    public List<FileTask> getAllFileTaskByFileActivityId(Long fileActivityId) {

        return repository.findAllByFileActivity_Id(fileActivityId);
    }

    public List<FileTask> getAllFileTaskByAssignedToId(Long assignedToId) {
        return repository.findAllByAssignedTo_Id(assignedToId);
    }

    public boolean changeStartDate(LocalDateTime startDate, Long fileTaskId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, fileTask -> {
            DenyOrProceed("UPDATE_FILE_TASK", fileTask);
            Database.startTransaction();
            fileTask.setStartDate(startDate);
            Database.commit();
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
            return true;
        });
    }

    public boolean changeDueDate(LocalDateTime dueDate, Long fileTaskId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, "UPDATE_FILE_TASK", fileTask -> {
            Database.startTransaction();
            fileTask.setDueDate(dueDate);
            Database.commit();
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
            return true;
        });
    }

    // permission inside
    public boolean changeTitle(String title, Long fileTaskId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, "UPDATE_FILE_TASK", fileTask -> {
            Database.startTransaction();
            fileTask.setTitle(title);
            Database.commit();
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
            return true;
        });
    }

    public DescriptionComment changeDescription(CommentInput commentInput) {
        var fileTask = Database.findOrThrow(FileTask.class, commentInput.getFileTask());
        DenyOrProceed("UPDATE_FILE_TASK", fileTask);
        Database.startTransaction();
        var description = fileTask.getDescription();
        if (description == null) {
            description = DescriptionComment.builder()
                    .fileTask(fileTask)
                    .agent(thisDBAgent())
                    .type(CommentType.Description)
                    .fileActivity(fileTask.getFileActivity())
                    .createdDate(LocalDateTime.now())
                    .build();
            Database.persist(description);
            fileTask.setDescription(description);
        }
        description.setContent(commentInput.getContent());
        TipTap.resolveCommentContent(description);
        Database.commit();
        new CommentEvent(EntityEvent.Type.UPDATED, description).fireToAll();
        new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
        return description;
    }

    public ReturnedComment changeRetour(CommentInput input) {
        return Database.findOrThrow(input.getFileTask().map(), fileTask -> {
            Database.startTransaction();
            DenyOrProceed("WORK_IN_FILE_TASK", fileTask);
            var retour = fileTask.getRetour();
            if (retour == null) {
                retour = ReturnedComment.builder()
                        .fileTask(fileTask)
                        .agent(thisDBAgent())
                        .type(CommentType.Retour)
                        .fileActivity(fileTask.getFileActivity())
                        .createdDate(LocalDateTime.now())
                        .build();
                Database.persist(retour);
                fileTask.setRetour(retour);
            }
            retour.setContent(input.getContent());
            TipTap.resolveCommentContent(retour);
            Database.commit();
            new CommentEvent(EntityEvent.Type.UPDATED, retour).fireToAll();
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
            return retour;
        });
    }

    // permission inside
    public ReturnedCause changeReturnedCause(Long fileTaskId, Long returnedCauseId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, fileTask -> {
            DenyOrProceed("WORK_IN_FILE_TASK", fileTask);
            var returnedCause = Database.findOrThrow(ReturnedCause.class, returnedCauseId);
            Database.startTransaction();
            fileTask.setReturnedCause(returnedCause);
            Database.commit();
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
            return returnedCause;
        });
    }

    // permission inside
    public TaskState changeState(Long fileTaskId, Long taskStateId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, fileTask -> {
            DenyOrProceed("WORK_IN_FILE_TASK", fileTask);
            var state = Database.findOrThrow(TaskState.class, taskStateId);
            Database.startTransaction();
            fileTask.setState(state);
            Database.commit();
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
            return state;
        });
    }

    // permission inside
    public boolean changeReturned(Long fileTaskId, boolean returned) {
        return Database.findOrThrow(FileTask.class, fileTaskId, "UPDATE_FILE_TASK", fileTask -> {
            Database.startTransaction();
            fileTask.setReturned(returned);
            Database.commit();
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
            return true;
        });
    }

    // permission inside
    public FileTask createChildFileTask(FileTaskInput input) {
        DenyOrProceed("CREATE_FILE_TASK", input);
        var parent = repository.findById(input.getParent().getId()).orElseThrow();
        var task = Database.findOrThrow(Task.class, input.getTask());
        var fileActivity = Database.findOrThrow(FileActivity.class, input.getFileActivity());
        var count = repository.countFileTaskByFileActivity_File_Id(fileActivity.getFile().getId());
        var fileTask = FileTask.builder()
                .fileActivity(fileActivity)
                .task(task)
                .toStartDate(LocalDateTime.now())
                .order((count + 1))
                .parent(parent)
                .reporter(Agent.thisDBAgent())
                .returned(true)
                .fileTaskSituations(new ArrayList<>())
                .build();
        var fileTaskSituation = FileTaskSituation.builder()
                .situation(task.getSituations().stream().filter(TaskSituation::isInitial).findFirst().orElseThrow())
                .fileTask(fileTask)
                .current(true)
                .build();
        fileTask.getFileTaskSituations().add(fileTaskSituation);
        repository.save(fileTask);
        new FileTaskEvent(EntityEvent.Type.ADDED, fileTask).fireToAll();
        return repository.findById(fileTask.getId()).orElseThrow();
    }

    public boolean changeParent(Long fileTaskId, Long parentId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, "UPDATE_FILE_TASK", fileTask -> {
            Database.startTransaction();
            safeRunWithFallback(
                    () -> fileTask.setParent(Database.findOrThrow(FileTask.class, parentId)),
                    () -> fileTask.setParent(null)
            );
            Database.commit();
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
            return true;
        });

    }

    public List<FileTask> getAllFileTaskByFileActivityIdInTrash(Long fileActivityId) {
        return repository.findAllByFileActivity_Id_In_Trash(fileActivityId);
    }

    public boolean recoverFileTaskFromTrash(Long fileTaskId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, "DELETE_FILE_TASK", fileTask -> {
            Database.startTransaction();
            fileTask.setInTrash(false);
            Database.commit();
            new FileTaskEvent(EntityEvent.Type.RECOVERED, fileTask).fireToAll();
            return true;
        });
    }

    public boolean sendFileTaskToTrash(Long fileTaskId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, "DELETE_FILE_TASK", fileTask -> {
            Database.startTransaction();
            fileTask.setInTrash(true);
            Database.commit();
            new FileTaskEvent(EntityEvent.Type.TRASHED, fileTask).fireToAll();
            return true;
        });
    }

    public List<Attachment> saveAttached(Long fileTaskId, DataFetchingEnvironment environment) throws IOException {
        ArrayList<ApplicationPart> files = environment.getArgument("attachments");
        var filesAttached = new ArrayList<FileTaskAttachment>();
        var fileTask = repository.findById(fileTaskId).orElseThrow();
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

    public boolean updateTitle(String title, long fileTaskId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, "UPDATE_FILE_TASK", fileTask -> {
            Database.startTransaction();
            fileTask.setTitle(title);
            Database.commit();
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
            return true;
        });
    }


    public boolean changeOrder(long fileTaskId, long fileTaskBeforeId) {
        var fileTask = Database.findOrThrow(FileTask.class, fileTaskId);
        var fileActivityId = fileTask.getFileActivity().getId();
        OrderManager.changeOrder(fileTask, Database.findOrNull(FileTask.class, fileTaskBeforeId), f -> f.getFileActivity().getId() == fileActivityId);
        return true;
    }
}