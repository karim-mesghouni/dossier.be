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
import com.softline.dossier.be.graphql.types.input.enums.FieldTypeInput;
import com.softline.dossier.be.repository.FileActivityRepository;
import com.softline.dossier.be.security.domain.Agent;
import graphql.GraphQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeParseException;
import java.util.List;

import static com.softline.dossier.be.Tools.Functions.safeValue;
import static com.softline.dossier.be.security.config.Gate.check;

@Service
@RequiredArgsConstructor
public class FileActivityService {
    private final FileActivityRepository repository;

    public List<FileActivity> getAll() {
        return Database.findAll(FileActivity.class);
    }

    public FileActivity getById(Long id) {
        return Database.findOrThrow(FileActivity.class, id);
    }


    public FileActivity create(FileActivityInput entityInput) {
        return Database.findOrThrow(File.class, entityInput.getFile(), file -> {
            var activity = Database.findOrThrow(Activity.class, entityInput.getActivity());
            var state = Database.query("SELECT s from ActivityState s where " +
                            "s.initial = true and s.activity.id = :actId", ActivityState.class)
                    .setParameter("actId", entityInput.getActivity().getId())
                    .setMaxResults(1)
                    .getSingleResult();
            Database.startTransaction();
            var fileActivity = Database.persist(FileActivity.builder()
                    .activity(activity)
                    .file(file)
                    .state(state)
                    .current(true)
                    .order(repository.getNextOrder(file.getId()))
                    .build());
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
            Database.commit();
            new FileActivityEvent(EntityEvent.Type.ADDED, fileActivity).fireToAll();
            return fileActivity;
        });
    }


    public List<FileActivity> getAllFileActivityByFileId(Long fileId) {
        return Database.em().createNamedQuery("FileActivity.getAllByFileId", FileActivity.class)
                .setParameter("fileId", fileId)
                .getResultList();
    }

    public ActivityState changeActivityState(Long activityStateId, Long fileActivityId) {
        return Database.findOrThrow(FileActivity.class, fileActivityId, "UPDATE_FILE_ACTIVITY", fileActivity -> {
            var activityState = Database.findOrThrow(ActivityState.class, activityStateId);
            Database.startTransaction();
            fileActivity.setState(activityState);
            Database.commit();
            new FileActivityEvent(EntityEvent.Type.UPDATED, fileActivity).fireToAll();
            return activityState;
        });
    }

    public ActivityDataField changeDataField(ActivityDataFieldInput input) {
        return Database.findOrThrow(input.map(), field -> {
            check("UPDATE_FILE_ACTIVITY_DATA_FIELD", field);
            Database.startTransaction();
            try {
                input.setFieldType(FieldTypeInput.valueOf(field.getFieldType().toString()));
                input.tryCastData();
            } catch (NumberFormatException | DateTimeParseException e) {
                // if data is not of the correct type
                Database.rollback();
                throw new GraphQLException(TextHelper.format("la valeur est malformée [{}]", field.getFieldType()));
            }
            field.setData(input.getData());
            field.setLastModifiedBy(Agent.thisDBAgent());
            Database.commit();
            new FileActivityDataFieldEvent(EntityEvent.Type.UPDATED, field).fireToAll();
            return field;
        });
    }

    public List<FileActivity> getAllFileActivityByFileIdInTrash(long fileId) {
        return Database.query("select distinct fa from FileActivity fa left join fa.fileTasks ft " +
                        "where fa.file.id = :fileId " +
                        "and (fa.inTrash = true or ft.inTrash = true or (fa.inTrash = true and size(fa.fileTasks) = 0))", FileActivity.class)
                .setParameter("fileId", fileId)
                .getResultList();
    }

    public boolean sendFileActivityToTrash(Long fileActivityId) {
        return Database.findOrThrow(FileActivity.class, fileActivityId, "DELETE_FILE_ACTIVITY", fileActivity -> {
            Database.startTransaction();
            fileActivity.setInTrash(true);
            Database.commit();
            new FileActivityEvent(EntityEvent.Type.TRASHED, fileActivity).fireToAll();
            return true;
        });
    }

    public boolean recoverFileActivityFromTrash(Long fileActivityId) {
        return Database.findOrThrow(FileActivity.class, fileActivityId, "DELETE_FILE_ACTIVITY", fileActivity -> {
            Database.startTransaction();
            fileActivity.setInTrash(false);
            Database.commit();
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
