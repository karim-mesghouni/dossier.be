package com.softline.dossier.be.graphql.types.input;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActivityStateInput
{
    long id;
    String name;

    boolean initial;
    boolean Final;

    ActivityInput activity;

    List<FileActivityInput> fileActivities;
}

