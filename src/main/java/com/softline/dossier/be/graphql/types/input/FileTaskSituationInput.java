package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.domain.FileTaskSituation;
import lombok.Getter;

import java.util.List;

@Getter
public class FileTaskSituationInput extends Input<FileTaskSituation> implements HasId {
    Class<FileTaskSituation> mappingTarget = FileTaskSituation.class;

    Long id;
    boolean current;
    FileTaskInput fileTask;
    TaskSituationInput situation;
    List<BlockingInput> blockings;
    BlockingInput blocking;
}
