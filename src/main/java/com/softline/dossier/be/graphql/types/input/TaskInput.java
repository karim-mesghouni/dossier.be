package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.domain.Task;
import lombok.Getter;

import java.util.List;

@Getter
public class TaskInput extends Input<Task> implements HasId {
    Class<Task> mappingTarget = Task.class;

    Long id;
    String name;
    String description;

    ActivityInput activity;

    List<TaskStateInput> states;
    List<TaskSituationInput> situations;
}
