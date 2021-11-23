package com.softline.dossier.be.graphql.types.input;


import com.softline.dossier.be.domain.ActivityState;
import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;

import java.util.List;

@Getter
public class ActivityStateInput extends Input<ActivityState> implements HasId {
    Class<ActivityState> mappingTarget = ActivityState.class;

    long id;
    String name;

    boolean initial;
    boolean Final;

    ActivityInput activity;

    List<FileActivityInput> fileActivities;
}

