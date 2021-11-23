package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileTaskSituationInput implements HasId {

    long id;
    boolean current;
    FileTaskInput fileTask;

    TaskSituationInput situation;
    List<BlockingInput> blockings;
}
