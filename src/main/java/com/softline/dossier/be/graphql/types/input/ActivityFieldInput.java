package com.softline.dossier.be.graphql.types.input;


import com.softline.dossier.be.domain.ActivityField;
import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.graphql.types.input.enums.FieldTypeInput;
import lombok.Getter;

@Getter
public class ActivityFieldInput extends Input<ActivityField> implements HasId {
    Class<ActivityField> mappingTarget = ActivityField.class;

    long id;
    String fieldName;

    FieldTypeInput fieldType;

    ActivityFieldGroupInput group;

    ActivityInput activity;
    String groupName;
}
