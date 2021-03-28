package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.DescriptionComment;
import com.softline.dossier.be.domain.TaskState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileTaskInput {

    Long id;
    boolean current;
    FileActivityInput fileActivity;
    TaskInput task;
    Date toStartDate;
    Date dueDate;
    Date startDate;
    Date endDate;
    AgentInput reporter;
    AgentInput assignedTo;
    List<FileTaskSituationInput> fileTaskStates;
    TaskStateInput state;
    FileTaskSituationInput  currentFileTaskSituation;
    String title;
    CommentInput description;
    CommentInput retour;
}
