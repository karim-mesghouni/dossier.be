package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.ActivityFieldGroup;
import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;

@Getter
public class ActivityFieldGroupInput extends Input<ActivityFieldGroup> implements HasId {
    Class<ActivityFieldGroup> mappingTarget = ActivityFieldGroup.class;

    Long id;
    String group;
}
