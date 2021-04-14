package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.domain.enums.CommentType;
import com.softline.dossier.be.graphql.types.input.ActivityDataFieldInput;
import com.softline.dossier.be.graphql.types.input.CommentInput;
import com.softline.dossier.be.graphql.types.input.FileTaskInput;
import com.softline.dossier.be.graphql.types.input.ReturnedCauseInput;
import com.softline.dossier.be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service
public class FileTaskService extends IServiceBase<FileTask, FileTaskInput, FileTaskRepository> {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskStateRepository fileTaskStateRepository;
    @Autowired
    FileTaskSituationRepository fileTaskSituationRepository;
    @Autowired
    TaskSituationRepository taskSituationRepository;
    @Autowired
    AgentRepository agentRepository;
    @Autowired
    TaskStateRepository taskStateRepository;
    @Autowired
    DescriptionCommentRepository descriptionCommentRepository;
    @Autowired
    ReturnedCommentRepository returnedCommentRepository;
    @Autowired
    ActivityDataFieldRepository activityDataFieldRepository;
    @Autowired
    FileActivityRepository fileActivityRepository;
    @Autowired
    ReturnedCauseRepository returnedCauseRepository;


    @Override
    public List<FileTask> getAll() {


        return repository.findAll();
    }

    @Override
    public FileTask create(FileTaskInput input) {
        var task = taskRepository.findById(input.getTask().getId()).orElseThrow();
        var fileActivity = fileActivityRepository.findById(input.getFileActivity().getId()).orElseThrow();
        var count = getRepository().countFileTaskByFileActivity_File_Id(fileActivity.getFile().getId());
        var fileTask = FileTask.builder()
                .fileActivity(fileActivity)
                .task(task)
                .toStartDate(new Date())
                .number((count + 1))
                .fileTaskSituations(new ArrayList<>())
                .build();
        var fileTaskSituation = FileTaskSituation.builder()
                .situation(task.getSituations().stream().filter(x -> x.isInitial()).findFirst().orElseThrow())
                .fileTask(fileTask)
                .current(true)
                .build();
        fileTask.getFileTaskSituations().add(fileTaskSituation);
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
        if (input.getState() != null && input.getState().getId() != null)
            fileTask.setState(TaskState.builder().id(input.getState().getId()).build());
        return fileTask;
    }

    @Override
    public boolean delete(long id) {
        repository.deleteById(id);
        return true;
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

    public Agent changeAssignedTo(Long assignedToId, Long fileTaskId) {
        var assigned = agentRepository.findById(assignedToId).orElseThrow();
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setAssignedTo(assigned);
        getRepository().save(fileTask);
        return assigned;
    }

    public Agent changeReporter(Long reporterId, Long fileTaskId) {
        var reporter = agentRepository.findById(reporterId).orElseThrow();
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setReporter(reporter);
        getRepository().save(fileTask);
        return reporter;
    }

    public FileTaskSituation changeFileTaskSituation(Long situationId, Long fileTaskId) {
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        var situation = taskSituationRepository.findById(situationId).orElseThrow();
        var oldSituation = fileTaskSituationRepository.findFirstByFileTaskAndCurrentIsTrue(fileTask);
        if (oldSituation != null) {
            if (oldSituation.getSituation().getId() == situationId) {
                return oldSituation;
            }
            oldSituation.setCurrent(false);
        }
        if (situation.isFinal()) {
            fileTask.setEndDate(new Date());
        } else if (!situation.isFinal() && !situation.isInitial()) {
            fileTask.setStartDate(new Date());
        }
        var fileSituation = fileTaskSituationRepository.save(FileTaskSituation.builder()
                .situation(situation)
                .current(true)
                .fileTask(fileTask).build());

        return fileSituation;
    }

    public List<FileTask> getAllFileTaskByFileActivityId(Long fileActivityId) {

        return getRepository().findAllByFileActivity_Id(fileActivityId);
    }

    public List<FileTask> getAllFileTaskByAssignedToId(Long assignedToId) {
        return getRepository().findAllByAssignedTo_Id(assignedToId);
    }

    public boolean changeToStartDate(Date toStartDate, Long fileTaskId) {
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setToStartDate(toStartDate);
        return true;
    }

    public boolean changeDueDate(Date dueDate, Long fileTaskId) {
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setDueDate(dueDate);
        return true;
    }

    public boolean changeTitle(String title, Long fileTaskId) {
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setTitle(title);
        return true;
    }

    public DescriptionComment changeDescription(CommentInput description) {
        if (description.getId() != null) {
            var descriptionExist = descriptionCommentRepository.findById(description.getId()).orElseThrow();
            descriptionExist.setContent(description.getContent());
            return descriptionExist;
        } else {
            var fileTask = getRepository().findById(description.getFileTask().getId()).orElseThrow();
            var fileActivity = fileActivityRepository.findById(description.getFileActivity().getId()).orElseThrow();

            var descriptionNew = descriptionCommentRepository.save(DescriptionComment.builder()
                    .fileActivity(fileActivity)
                    .content(description.getContent())
                    .fileTask(fileTask)
                    .agent(agentRepository.findById(description.getAgent().getId()).orElseThrow())
                    .build()
            );
            fileTask.setDescription(descriptionNew);
            descriptionNew.setType(CommentType.Description);

            return descriptionNew;
        }
    }

    public ReturnedComment changeRetour(CommentInput retour) {
        if (retour.getId() != null) {
            var retourExist = returnedCommentRepository.findById(retour.getId()).orElseThrow();
            retourExist.setContent(retour.getContent());
            return retourExist;
        } else {
            var fileTask = getRepository().findById(retour.getFileTask().getId()).orElseThrow();
            var fileActivity = fileActivityRepository.findById(retour.getFileActivity().getId()).orElseThrow();

            var retourNew = returnedCommentRepository.save(ReturnedComment.builder()
                    .fileActivity(fileActivity)
                    .content(retour.getContent())
                    .fileTask(fileTask)
                    .agent(agentRepository.findById(retour.getAgent().getId()).orElseThrow())
                    .build()
            );
            fileTask.setRetour(retourNew);
            retourNew.setType(CommentType.Returned);
            return retourNew;
        }
    }

    public boolean changeDataField(ActivityDataFieldInput input) {
        var field = activityDataFieldRepository.findById(input.getId()).orElseThrow();
        field.setData(input.getData());
        return true;
    }

    public List<ReturnedCause> getAllReturnedCause() {
        return returnedCauseRepository.findAll();
    }

    public ReturnedCause changeReturnedCause(Long fileTaskId, Long returnedCauseId) {
        var returnedCause = returnedCauseRepository.findById(returnedCauseId).orElseThrow();
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setReturnedCause(returnedCause);
        getRepository().save(fileTask);
        return returnedCause;
    }

    public TaskState changeState(Long fileTaskId, Long taskStateId) {
        var taskState = taskStateRepository.findById(taskStateId).orElseThrow();
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setState(taskState);
        getRepository().save(fileTask);
        return taskState;
    }

    public boolean changeReturned(Long fileTaskId, boolean returned) {
        var fileTask = getRepository().findById(fileTaskId).orElseThrow();
        fileTask.setReturned(returned);
        getRepository().save(fileTask);
        return returned;
    }

    public FileTask createChildFileTask(FileTaskInput input) {
        var parent=getRepository().findById(input.getParent().getId()).orElseThrow();
        var task = taskRepository.findById(input.getTask().getId()).orElseThrow();
        var fileActivity = fileActivityRepository.findById(input.getFileActivity().getId()).orElseThrow();
        var count = getRepository().countFileTaskByFileActivity_File_Id(fileActivity.getFile().getId());
        var fileTask = FileTask.builder()
                .fileActivity(fileActivity)
                .task(task)
                .toStartDate(new Date())
                .number((count + 1))
                .parent(parent)
                .returned(true)
                .fileTaskSituations(new ArrayList<>())
                .build();
        var fileTaskSituation = FileTaskSituation.builder()
                .situation(task.getSituations().stream().filter(x -> x.isInitial()).findFirst().orElseThrow())
                .fileTask(fileTask)
                .current(true)
                .build();
        fileTask.getFileTaskSituations().add(fileTaskSituation);
         getRepository().save(fileTask);
    return   getRepository().findById(fileTask.getId()).orElseThrow();
    }

    public FileTask changeParent(Long fileTaskId, Long parentId) {
        var fileTask=getRepository().findById(fileTaskId).orElseThrow();
        var parent=getRepository().findById(parentId).orElseThrow();
        fileTask.setParent(parent);
        return  fileTask;
    }
}