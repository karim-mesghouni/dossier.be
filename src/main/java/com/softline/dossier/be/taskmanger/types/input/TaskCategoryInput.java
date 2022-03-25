package com.softline.dossier.be.taskmanger.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.graphql.types.input.Input;
import com.softline.dossier.be.taskmanger.domain.GenericTask;
import com.softline.dossier.be.taskmanger.domain.TaskCategory;
import lombok.Getter;

@Getter
public class TaskCategoryInput extends Input<TaskCategory> implements HasId {
    String name;
    Long id;
    Class<TaskCategory> mappingTarget = TaskCategory.class;
}
