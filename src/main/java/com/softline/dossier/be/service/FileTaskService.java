package com.softline.dossier.be.service;

import com.softline.dossier.be.Tools.ListUtils;
import com.softline.dossier.be.Tools.TextHelper;
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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.softline.dossier.be.Tools.Functions.*;
import static com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator.DenyOrProceed;
import static com.softline.dossier.be.security.domain.Agent.thisDBAgent;

@Service
@RequiredArgsConstructor
public class FileTaskService {
    private final FileTaskSituationRepository fileTaskSituationRepository;
    private final FileTaskAttachmentRepository fileTaskAttachmentRepository;
    private final FileTaskRepository repository;

    public FileTask getById(Long id) {
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
        var task = Database.findOrThrow(input.getTask().map());
        var fileActivity = Database.findOrThrow(input.getFileActivity().map());
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
                .situation(ListUtils.filterFirstStrict(task.getSituations(), TaskSituation::isInitial))
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

    public FileTaskSituation changeFileTaskSituation(Long situationId, Long fileTaskId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, fileTask -> {
            DenyOrProceed("WORK_IN_FILE_TASK", fileTask);
            Database.startTransaction();
            var situation = Database.findOrThrow(TaskSituation.class, situationId);
            if (fileTask.getCurrentFileTaskSituation().getSituation().isBlock()) {
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

    public TaskState changeState(Long fileTaskId, Long taskStateId) {
        var fileTask = Database.findOrThrow(FileTask.class, fileTaskId);
        DenyOrProceed("WORK_IN_FILE_TASK", fileTask);
        var state = Database.findOrNull(TaskState.class, taskStateId);
        if (state != null && !fileTask.getTask().getStates().contains(state)) {
            throw new GraphQLException("The given state does not belong to this fileTask");
        }
        Database.inTransaction(() -> fileTask.setState(state));
        new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
        return state;
    }

    public boolean changeReturned(Long fileTaskId, boolean returned) {
        return Database.findOrThrow(FileTask.class, fileTaskId, "UPDATE_FILE_TASK", fileTask -> {
            Database.inTransaction(() -> fileTask.setReturned(returned));
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
            return true;
        });
    }

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
                .situation(ListUtils.filterFirstStrict(task.getSituations(), TaskSituation::isInitial))
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
            Database.inTransaction(() -> safeRunWithFallback(
                    () -> fileTask.setParent(Database.findOrThrow(FileTask.class, parentId)),
                    () -> fileTask.setParent(null)
            ));
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
            return true;
        });

    }

    public List<FileTask> getAllFileTaskByFileActivityIdInTrash(Long fileActivityId) {
        return repository.findAllByFileActivity_Id_In_Trash(fileActivityId);
    }

    public boolean recoverFileTaskFromTrash(Long fileTaskId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, "DELETE_FILE_TASK", fileTask -> {
            Database.inTransaction(() -> fileTask.setInTrash(false));
            new FileTaskEvent(EntityEvent.Type.RECOVERED, fileTask).fireToAll();
            return true;
        });
    }

    public boolean sendFileTaskToTrash(Long fileTaskId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, "DELETE_FILE_TASK", fileTask -> {
            Database.inTransaction(() -> fileTask.setInTrash(true));
            new FileTaskEvent(EntityEvent.Type.TRASHED, fileTask).fireToAll();
            return true;
        });
    }

    public List<Attachment> saveAttached(Long fileTaskId, DataFetchingEnvironment environment) {
        ArrayList<ApplicationPart> files = environment.getArgument("attachments");
        var filesAttached = new ArrayList<FileTaskAttachment>();
        Database.startTransaction();
        var fileTask = Database.findOrThrow(FileTask.class, fileTaskId);
        if (new Random().nextInt(100) > 80) {
            // TODO: check for unlinked files and remove them (files that exist on disk but not on database, due to failure)
        }
        for (ApplicationPart requestFile : files) {
            var fta = new FileTaskAttachment();
            fta.setFileTask(fileTask);
            fta.resolveFromApplicationPart(requestFile);
            filesAttached.add(fta);
            Database.persist(fta);
        }
        Database.commit();
        return filesAttached.stream().map(e -> (Attachment) e).collect(Collectors.toList());
    }

    public void removeCheckSheet(Long checkSheetId) {
        var em = Database.em();
        var ch = em.find(CheckSheet.class, checkSheetId);
        em.remove(ch);
    }

    public CheckSheet setCheckSheet(Long fileTaskId, DataFetchingEnvironment environment) {
        Database.startTransaction();
        var fileTask = Database.findOrThrow(FileTask.class, fileTaskId);
        if (fileTask.getCheckSheet() != null) {
            throw new GraphQLException("Cette tâche a déjà une fiche de contrôle");
        }
        ApplicationPart fileSheet = environment.getArgument("file");
        var sheet = wrap(() -> new XSSFWorkbook(fileSheet.getInputStream()), (e) -> new RuntimeException("Impossible de charger le fichier en tant que document XSSF"))
                .getSheetAt(0);
        var errors = new HashMap<Integer, Throwable>();
        var invalide = new ArrayList<String[]>();
        var currentGroup = "";
        for (int i = 16; i < sheet.getPhysicalNumberOfRows(); i++) {
            try {
                var row = sheet.getRow(i);
                var NON_OK_CELL = row.getCell(29);
                if (NON_OK_CELL == null) {
                    break;
                } else if (Objects.equals(NON_OK_CELL.getCellType(), CellType.BOOLEAN)) {
                    if (NON_OK_CELL.getBooleanCellValue()) {
                        invalide.add(new String[]{currentGroup, row.getCell(1).getStringCellValue(), row.getCell(12).getStringCellValue()});
                    }
                } else if (Objects.equals(NON_OK_CELL.getCellType(), CellType.FORMULA)) {
                    currentGroup = row.getCell(0).getStringCellValue();
                    //noinspection UnnecessaryContinue
                    continue;// This is the %percentage text of the sum of this group
                } else {
                    // This is an error
                    throw new IOException(TextHelper.format("Expected cell type FORMULA or BOOLEAN got: {}", NON_OK_CELL.getCellType()));
                }
            } catch (Throwable e) {
                errors.put(i + 1, e);
            }
        }
        if (!errors.isEmpty()) {
            throw new GraphQLException(TextHelper.format("Il y a une erreur à la ligne {} ({})", errors.keySet().stream().findFirst().get(), errors.values().stream().findFirst().get()));
        }
        AtomicBoolean sendEvent = new AtomicBoolean(false);
        fileTask.setCheckSheet(new CheckSheet(fileTask, new ArrayList<>()));
        fileTask.getCheckSheet().resolveFromApplicationPart(fileSheet);
        invalide.forEach(i -> {
            sendEvent.set(true);
            fileTask.getCheckSheet()
                    .getInvalidItems()
                    .add(new CheckItem(fileTask.getCheckSheet(), i[0], i[1], i[2]));
        });
        Database.commit();
        if (sendEvent.get()) {
            new FileTaskEvent(EntityEvent.Type.UPDATED, fileTask).fireToAll();
        }
        return fileTask.getCheckSheet();
    }

    public boolean deleteAttached(Long attachedId) {
        Database.removeNow(FileTaskAttachment.class, attachedId);

        return true;
    }

    public boolean updateTitle(String title, long fileTaskId) {
        return Database.findOrThrow(FileTask.class, fileTaskId, "UPDATE_FILE_TASK", fileTask -> {
            Database.inTransaction(() -> fileTask.setTitle(title));
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