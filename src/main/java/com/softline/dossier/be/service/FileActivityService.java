package com.softline.dossier.be.service;

import com.softline.dossier.be.Tools.TextHelper;
import com.softline.dossier.be.database.Database;
import com.softline.dossier.be.database.OrderManager;
import com.softline.dossier.be.domain.*;
import com.softline.dossier.be.events.EntityEvent;
import com.softline.dossier.be.events.entities.FileActivityDataFieldEvent;
import com.softline.dossier.be.events.entities.FileActivityEvent;
import com.softline.dossier.be.graphql.types.input.ActivityDataFieldInput;
import com.softline.dossier.be.graphql.types.input.FileActivityInput;
import com.softline.dossier.be.repository.FileActivityRepository;
import graphql.GraphQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.softline.dossier.be.Tools.Functions.safeValue;

@Transactional
@Service
@RequiredArgsConstructor
public class FileActivityService extends IServiceBase<FileActivity, FileActivityInput, FileActivityRepository> {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FileActivity> getAll() {
        return repository.findAll();
    }

    @Override
    public FileActivity create(FileActivityInput entityInput) {
        return Database.findOrThrow(File.class, entityInput.getFile(), file -> {
            var activity = Database.findOrThrow(Activity.class, entityInput.getActivity());
            var state = Database.em()
                    .createQuery("SELECT s from ActivityState s where " +
                            "s.initial = true and s.activity.id = :actId", ActivityState.class)
                    .setParameter("actId", entityInput.getActivity().getId())
                    .setMaxResults(1)
                    .getSingleResult();
            var fileActivity = Database.persist(FileActivity.builder()
                    .activity(activity)
                    .file(file)
                    .state(state)
                    .current(true)
                    .order(getRepository().getNextOrder(file.getId()))
                    .build());
            Database.flush();
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
            Database.flush();
            new FileActivityEvent(EntityEvent.Type.ADDED, fileActivity).fireToAll();
            return fileActivity;
        });
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

    public ActivityState changeActivityState(Long activityStateId, Long fileActivityId) {
        return Database.findOrThrow(FileActivity.class, fileActivityId, "UPDATE_FILE_ACTIVITY", fileActivity -> {
            var activityState = Database.findOrThrow(ActivityState.class, activityStateId);
            fileActivity.setState(activityState);
            Database.flush();
            new FileActivityEvent(EntityEvent.Type.UPDATED, fileActivity).fireToAll();
            return activityState;
        });
    }

    public boolean changeDataField(ActivityDataFieldInput input) {
        return Database.findOrThrow(ActivityDataField.class, input, "UPDATE_FILE_ACTIVITY_DATA_FIELD", field -> {
            try {
                input.tryCastData();
            } catch (NumberFormatException | DateTimeParseException e) {
                // if data is not of the correct type
                throw new GraphQLException(TextHelper.format("la valeur est malformée({})", field.getFieldType()));
            }
            field.setData(input.getData());
            Database.flush();
            new FileActivityDataFieldEvent(EntityEvent.Type.UPDATED, field).fireToAll();
            return true;
        });
    }

    public List<FileActivity> getAllFileActivityByFileIdInTrash(long fileId) {
        return entityManager.createQuery("select distinct fa from FileActivity fa inner join fa.fileTasks ft " +
                        "where fa.file.id = :fileId " +
                        "and (fa.inTrash = true or ft.inTrash = true or (fa.inTrash = true and size(fa.fileTasks) = 0))", FileActivity.class)
                .setParameter("fileId", fileId)
                .getResultList();
    }

    public boolean sendFileActivityToTrash(Long fileActivityId) {
        return Database.findOrThrow(FileActivity.class, fileActivityId, "DELETE_FILE_ACTIVITY", fileActivity -> {
            fileActivity.setInTrash(true);
            Database.flush();
            new FileActivityEvent(EntityEvent.Type.TRASHED, fileActivity).fireToAll();
            return true;
        });
    }

    public boolean recoverFileActivityFromTrash(Long fileActivityId) {
        return Database.findOrThrow(FileActivity.class, fileActivityId, "DELETE_FILE_ACTIVITY", fileActivity -> {
            fileActivity.setInTrash(false);
            Database.flush();
            new FileActivityEvent(EntityEvent.Type.RECOVERED, fileActivity).fireToAll();
            return true;
        });
    }

    public boolean changeOrder(long fileActivityId, long fileActivityBeforeId) {
        var fileActivity = Database.findOrThrow(FileActivity.class, fileActivityId);
        var fileId = fileActivity.getFile().getId();
        OrderManager.changeOrder(fileActivity, Database.findOrNull(FileActivity.class, fileActivityBeforeId), fa -> fa.getFile().getId() == fileId);
        return true;
    }
}
