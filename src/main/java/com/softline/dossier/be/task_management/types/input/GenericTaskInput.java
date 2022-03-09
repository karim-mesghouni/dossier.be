package com.softline.dossier.be.task_management.types.input;


import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.graphql.types.input.Input;
import com.softline.dossier.be.task_management.domain.GenericTask;

import com.softline.dossier.be.task_management.domain.TaskCategory;
import com.softline.dossier.be.task_management.enums.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class GenericTaskInput extends Input<GenericTask> implements HasId {
    Long id;
    String name;
    String description;
    LocalDateTime toStartDate;
    LocalDateTime dueDate;
    LocalDateTime startDate;
    LocalDateTime endDate;
    TaskCategory category;
//    List<DaysOfWeek> daysOfWeek;
//    List<Months> months;
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Class<GenericTask> getMappingTarget() {
        return GenericTask.class;
    }

}
