package com.softline.dossier.be.graphql.types.input;


import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;

import java.util.List;

@Getter
public class ActivityInput extends Input<Activity> implements HasId {
    Class<Activity> mappingTarget = Activity.class;

    Long id;
    String name;
    String description;

    List<ActivityFieldInput> fields;

    List<FileActivityInput> fileActivities;

    List<TaskInput> tasks;
    List<ActivityStateInput> states;
}
