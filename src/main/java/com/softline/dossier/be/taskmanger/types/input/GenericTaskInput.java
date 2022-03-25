package com.softline.dossier.be.taskmanger.types.input;


import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.graphql.types.input.AgentInput;
import com.softline.dossier.be.graphql.types.input.Input;
import com.softline.dossier.be.security.domain.Agent;
import com.softline.dossier.be.taskmanger.converter.IntegerListToStringConverter;
import com.softline.dossier.be.taskmanger.converter.StringListToStringConverter;
import com.softline.dossier.be.taskmanger.domain.GenericTask;

import com.softline.dossier.be.taskmanger.domain.TaskCategory;
import com.softline.dossier.be.taskmanger.enums.Periodicity;
import com.softline.dossier.be.taskmanger.enums.PeriodicityType;
import lombok.Getter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class GenericTaskInput extends Input<GenericTask> implements HasId {
    Class<GenericTask> mappingTarget = GenericTask.class;
    Long id;
    String name;
    String description;
    Periodicity periodicity;
    TaskCategoryInput category;
    PeriodicityType periodicityType;
    List<String> months;
    List<String> daysOfWeek;
    List<Integer> daysOfMonth;
    LocalDateTime toStartDate;
    LocalDateTime dueDate;
    AgentInput reporter;
    AgentInput assignedTo;
}
