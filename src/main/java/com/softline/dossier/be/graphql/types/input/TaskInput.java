package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskInput implements HasId {


    long id;
    String name;
    String description;

    ActivityInput activity;

    List<TaskStateInput> states;
    List<TaskSituationInput> situations;
}
