package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskInput {


    long id;
    String name;
    String description;

    ActivityInput activity;

    List<JobInput> jobs;
    List<TaskStateInput> states;
    List<TaskSituationInput> situations;
}
