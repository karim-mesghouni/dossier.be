package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileTaskInput {
    long id;
    boolean current;
    FileActivityInput fileActivity;
    TaskInput task;
    LocalDateTime toStartDate;
    LocalDateTime dueDate;
    LocalDateTime startDate;
    LocalDateTime endDate;
    AgentInput reporter;
    AgentInput assignedTo;
    List<FileTaskSituationInput> fileTaskStates;
    TaskStateInput state;
    FileTaskSituationInput currentFileTaskSituation;
    String title;
    CommentInput description;
    CommentInput retour;
    ReturnedCauseInput returnedCause;
    boolean returned;
    FileTaskInput parent;
    List<AttachmentInput> attachments;
}
