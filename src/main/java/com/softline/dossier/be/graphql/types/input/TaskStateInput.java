package com.softline.dossier.be.graphql.types.input;


import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.domain.TaskState;
import lombok.Getter;

import java.util.List;

@Getter
public class TaskStateInput extends Input<TaskState> implements HasId {
    Class<TaskState> mappingTarget = TaskState.class;

    long id;
    String name;

    TaskInput task;
    List<FileTaskInput> fileTasks;
}
