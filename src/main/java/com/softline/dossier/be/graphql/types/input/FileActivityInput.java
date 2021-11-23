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
public class FileActivityInput implements HasId {
    int order;
    ActivityStateInput state;
    boolean current;
    long id;
    FileInput file;
    ActivityInput activity;
    List<ActivityDataFieldInput> dataFields;
    List<FileTaskInput> fileTasks;
}
