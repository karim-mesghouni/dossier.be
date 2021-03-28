package com.softline.dossier.be.graphql.types.input;


import com.softline.dossier.be.domain.ActivityFieldGroup;
import com.softline.dossier.be.graphql.types.input.enums.FieldTypeInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Transient;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityFieldInput {

    long id;
    String fieldName;

    FieldTypeInput fieldType;

    ActivityFieldGroupInput group;

    ActivityInput activity;
    String groupName;
}
