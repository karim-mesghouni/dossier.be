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
public class FileTaskSituationInput
{

    long id;
    boolean current;
    FileTaskInput fileTask;

    TaskSituationInput situation;
    List<BlockingInput> blockings;
}
