package com.softline.dossier.be.service;

import com.softline.dossier.be.domain.ActivityDataField;
import com.softline.dossier.be.domain.ActivityState;
import com.softline.dossier.be.domain.File;
import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.domain.enums.FieldType;
import com.softline.dossier.be.events.FileActivityEvent;
import com.softline.dossier.be.events.types.EntityEvent;
import com.softline.dossier.be.graphql.types.input.FileActivityInput;
import com.softline.dossier.be.repository.ActivityRepository;
import com.softline.dossier.be.repository.ActivityStateRepository;
import com.softline.dossier.be.repository.FileActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class FileActivityService extends IServiceBase<FileActivity, FileActivityInput, FileActivityRepository> {
    @Autowired
    ActivityStateRepository activityStateRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FileActivity> getAll() {
        return repository.findAll();
    }

    @Override
    public FileActivity create(FileActivityInput entityInput) {
        var fileActivityOrder = getRepository().getMaxOrder();
        if (fileActivityOrder == null) {
            fileActivityOrder = 0;
        }
        fileActivityOrder++;
        var activity = activityRepository.getOne(entityInput.getActivity().getId());
        var fileActivity = FileActivity.builder()
                .activity(activity)
                .file(File.builder().id(entityInput.getFile().getId()).build())
                .state(activityStateRepository.findFirstByInitialIsTrueAndActivity_Id(entityInput.getActivity().getId()))
                .current(true)
                .order(fileActivityOrder)
                .build();
        if (entityInput.getDataFields() != null && !entityInput.getDataFields().isEmpty()) {
            var dataFields = entityInput.getDataFields().stream()
                    .map(x -> ActivityDataField.builder()
                            .data(x.getData())
                            .groupName(x.getGroupName())
                            .fieldName(x.getFieldName())
                            .fieldType(FieldType.valueOf(x.getFieldType().toString()))
                            .fileActivity(fileActivity)
                            .build()
                    );
            fileActivity.setDataFields(dataFields.collect(Collectors.toList()));
        }
        repository.save(fileActivity);
        new FileActivityEvent(EntityEvent.Event.ADDED, fileActivity).fireToAll();
        return repository.getOne(fileActivity.getId());
    }

    @Override
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
        return repository.findById(id).orElseThrow();
    }

    public List<FileActivity> getAllFileActivityByFileId(Long fileId) {
        return getRepository().findAllByFile_Id(fileId);
    }

    public ActivityState changeActivityState(Long activityStateId, Long fileActivityId) {
        var activityState = activityStateRepository.findById(activityStateId).orElseThrow();
        var fileActivity = getRepository().findById(fileActivityId).orElseThrow();
        fileActivity.setState(activityState);
        return activityState;
    }

    public List<FileActivity> getAllFileActivityByFileIdInTrash(long fileId) {
        return entityManager.createQuery("select distinct fa from FileActivity fa inner join fa.fileTasks ft " +
                        "where fa.file.id = :fileId " +
                        "and (fa.inTrash=true or ft.inTrash=true)", FileActivity.class)
                .setParameter("fileId", fileId)
                .getResultList();
    }

    public boolean sendFileActivityToTrash(Long fileActivityId) {
        var fileActivity = getRepository().getOne(fileActivityId);
        fileActivity.setInTrash(true);
        new FileActivityEvent(EntityEvent.Event.TRASHED, fileActivity);
        return true;
    }

    public boolean recoverFileActivityFromTrash(Long fileActivityId) {
        var fileActivity = getRepository().getOne(fileActivityId);
        fileActivity.setInTrash(false);
        new FileActivityEvent(EntityEvent.Event.RECOVERED, fileActivity);
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
    public synchronized boolean changeOrder(long fileActivityId, long fileActivityBeforeId) {
        if (repository.count() < 2) {
            return true;// this should not happen
        }
        var fileActivity = repository.findById(fileActivityId).orElseThrow();
        var fileId = fileActivity.getFile().getId();
        var res = repository.findById(fileActivityBeforeId);
        // TODO: convert this logic into Mutating queries in JPA
        if (res.isPresent()) {
            var fileActivityBefore = res.get();
            // how many fileActivitys will be updated (increment or decrement their order)
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
        } else {// fileActivity should be the first item in the list
            var allBefore = repository.findAllByOrderBefore(fileActivity.getOrder(), fileId);
            fileActivity.setOrder(allBefore.stream().findFirst().get().getOrder());// gets the order of the old first file
            allBefore.forEach(FileActivity::incrementOrder);
        }
        return true;
    }
}
