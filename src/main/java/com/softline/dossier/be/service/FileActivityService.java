package com.softline.dossier.be.service;

import com.softline.dossier.be.Tools.Database;
import com.softline.dossier.be.domain.ActivityDataField;
import com.softline.dossier.be.domain.ActivityState;
import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.graphql.types.input.ActivityDataFieldInput;
import com.softline.dossier.be.graphql.types.input.FileActivityInput;
import com.softline.dossier.be.repository.*;
import com.softline.dossier.be.service.exceptions.ClientReadableException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.softline.dossier.be.Application.context;
import static com.softline.dossier.be.SSE.EventController.silently;
import static com.softline.dossier.be.Tools.Functions.safeValue;
import static com.softline.dossier.be.Tools.TextHelper.format;
import static com.softline.dossier.be.security.config.AttributeBasedAccessControlEvaluator.cannot;

@Transactional
@Service
@RequiredArgsConstructor
public class FileActivityService extends IServiceBase<FileActivity, FileActivityInput, FileActivityRepository> {
    private final ActivityStateRepository activityStateRepository;
    private final ActivityRepository activityRepository;
    private final ActivityDataFieldRepository activityDataFieldRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FileActivity> getAll() {
        return repository.findAll();
    }

    @Override
    public FileActivity create(FileActivityInput entityInput) {
        var fileId = entityInput.getFile().getId();
        var file = context().getBean(FileRepository.class).findById(fileId).orElseThrow();// validate file exists
        var activity = activityRepository.findById(entityInput.getActivity().getId()).orElseThrow();
        var fileActivity = FileActivity.builder()
                .activity(activity)
                .file(file)
                .state(activityStateRepository.findFirstByInitialIsTrueAndActivity_Id(entityInput.getActivity().getId()))
                .current(true)
                .order(getRepository().getNextOrder(fileId))
                .build();

        repository.save(fileActivity);
        activity.getFields().forEach(field -> {
            fileActivity.getDataFields().add(
                    ActivityDataField.builder()
                            .fieldType(field.getFieldType())
                            .fieldName(field.getFieldName())
                            .groupName(safeValue(() -> field.getGroup().getName()))
                            .fileActivity(fileActivity)
                            .data(null)
                            .build()
            );
        });
        repository.saveAndFlush(fileActivity);
        return fileActivity;
    }

    @Override
    @PreAuthorize("hasPermission(#entityInput.id, 'FileActivity', 'UPDATE_FILE_ACTIVITY')")
    public FileActivity update(FileActivityInput entityInput) {
        return null;
    }

    @Override
    public boolean delete(long id) {
        repository.deleteById(id);
        return true;
    }

    @Override
    public FileActivity getById(long id) {
        return Database.findOrThrow(FileActivity.class, id);
    }

    public List<FileActivity> getAllFileActivityByFileId(Long fileId) {
        return getRepository().findAllByFile_Id(fileId);
    }

    @PreAuthorize("hasPermission(#fileActivityId, 'FileActivity', 'UPDATE_FILE_ACTIVITY')")
    public ActivityState changeActivityState(Long activityStateId, Long fileActivityId) {
        var activityState = activityStateRepository.findById(activityStateId).orElseThrow();
        var fileActivity = getRepository().findById(fileActivityId).orElseThrow();
        fileActivity.setState(activityState);
        return activityState;
    }

    public boolean changeDataField(ActivityDataFieldInput input) throws ClientReadableException {
        var field = activityDataFieldRepository.findById(input.getId()).orElseThrow();
        if (cannot("UPDATE_FILE_ACTIVITY", field.getFileActivity()) && cannot("UPDATE_FILE_ACTIVITY_DATA_FIELD", field.getFileActivity())) {
            throw new AccessDeniedException("Access Denied");
        }
        try {
            input.tryCastData();
        } catch (NumberFormatException | DateTimeParseException e) {
            // if data is not of the correct type
            throw new ClientReadableException(format("la valeur est malformée({})", field.getFieldType()));
        }
        field.setData(input.getData());
        return true;
    }

    public List<FileActivity> getAllFileActivityByFileIdInTrash(long fileId) {
        return entityManager.createQuery("select distinct fa from FileActivity fa inner join fa.fileTasks ft " +
                        "where fa.file.id = :fileId " +
                        "and (fa.inTrash=true or ft.inTrash=true)", FileActivity.class)
                .setParameter("fileId", fileId)
                .getResultList();
    }

    @PreAuthorize("hasPermission(#fileActivityId, 'FileActivity', 'DELETE_FILE_ACTIVITY')")
    public boolean sendFileActivityToTrash(Long fileActivityId) {
        var fileActivity = getRepository().findById(fileActivityId).orElseThrow();
        fileActivity.setInTrash(true);
        return true;
    }

    @PreAuthorize("hasPermission(#fileActivityId, 'FileActivity', 'DELETE_FILE_ACTIVITY')")
    public boolean recoverFileActivityFromTrash(Long fileActivityId) {
        var fileActivity = getRepository().findById(fileActivityId).orElseThrow();
        fileActivity.setInTrash(false);
        return true;
    }

    /**
     * change the order of a fileActivity,
     * will be called when the user changes the order of a fileActivity in the FilesView
     * in the case when fileActivityBeforeId is not existent the fileActivity will be moved to be the first item in the list
     *
     * @param fileActivityId       the fileActivity(id) that we want to change its order
     * @param fileActivityBeforeId the fileActivity(id) which should be before the new position of the fileActivity, may be non-existent
     * @return boolean
     */
    @Transactional
    public synchronized boolean changeOrder(long fileActivityId, long fileActivityBeforeId) {
        return silently(() -> {
            if (repository.count() < 2) {
                return true;// this should not happen
            }
            var fileActivity = repository.findById(fileActivityId).orElseThrow();
            var fileId = fileActivity.getFile().getId();
            repository.findById(fileActivityBeforeId).ifPresentOrElse(fileActivityBefore -> {
                // how many fileActivities will be updated (increment or decrement their order)
                var levelsChange = repository.countAllByOrderBetween(fileActivity.getOrder(), fileActivityBefore.getOrder(), fileId);
                if (fileActivity.getOrder() < fileActivityBefore.getOrder()) {// fileActivity is moving down the list
                    repository.findAllByOrderAfter(fileActivity.getOrder(), fileId)
                            .stream()
                            .limit(levelsChange + 1)
                            .forEach(FileActivity::decrementOrder);
                    fileActivity.setOrder(fileActivityBefore.getOrder() + 1);
                } else {// fileActivity is moving up the list
                    var allAfter = repository.findAllByOrderAfter(fileActivityBefore.getOrder(), fileId);
                    allAfter.stream()
                            .limit(levelsChange)
                            .forEach(FileActivity::incrementOrder);
                    fileActivity.setOrder(repository.findAllByOrderAfter(fileActivityBefore.getOrder(), fileId).stream().findFirst().get().getOrder() - 1);
                }
            }, () -> {
                // fileActivity should be the first item in the list
                repository.findAllByOrderBefore(fileActivity.getOrder(), fileId).forEach(FileActivity::incrementOrder);
                fileActivity.setOrder(repository.getMinOrder(fileId) - 1);
            });
            return true;
        });
    }
}
