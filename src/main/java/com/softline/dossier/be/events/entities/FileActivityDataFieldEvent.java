package com.softline.dossier.be.events.entities;

import com.softline.dossier.be.domain.ActivityDataField;
import com.softline.dossier.be.events.EntityEvent;

import static com.softline.dossier.be.Tools.Functions.safeValue;

public class FileActivityDataFieldEvent extends EntityEvent<ActivityDataField> {

    public FileActivityDataFieldEvent(Type type, ActivityDataField field) {
        super("activityDataField" + type, field);
        addData("activityDataFieldId", field.getId());
        addData("activityId", safeValue(() -> field.getFileActivity().getId()));
    }
}
