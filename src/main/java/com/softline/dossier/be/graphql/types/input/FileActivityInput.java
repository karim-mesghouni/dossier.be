package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.domain.FileActivity;
import lombok.Getter;

import java.util.List;

@Getter
public class FileActivityInput extends Input<FileActivity> implements HasId {
    Class<FileActivity> mappingTarget = FileActivity.class;

    int order;
    ActivityStateInput state;
    boolean current;
    Long id;
    FileInput file;
    ActivityInput activity;
    List<ActivityDataFieldInput> dataFields;
    List<FileTaskInput> fileTasks;
}
