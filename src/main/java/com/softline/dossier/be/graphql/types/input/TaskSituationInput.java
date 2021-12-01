package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.TaskSituation;
import lombok.Getter;

import java.util.List;

@Getter
public class TaskSituationInput extends Input<TaskSituation> {
    Class<TaskSituation> mappingTarget = TaskSituation.class;

    long id;
    String state;
    boolean initial;
    boolean Final;

    TaskInput task;

    List<FileTaskSituationInput> fileTaskSituations;
    FileTaskSituationInput fileTaskSituation;
}
