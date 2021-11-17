package com.softline.dossier.be.graphql.types.input;


import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityInput implements HasId {
    long id;
    String name;
    String description;

    List<ActivityFieldInput> fields;

    List<FileActivityInput> fileActivities;

    List<TaskInput> tasks;
    List<ActivityStateInput> states;
}
