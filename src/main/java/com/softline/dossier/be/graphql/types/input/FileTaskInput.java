package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import com.softline.dossier.be.domain.FileTask;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class FileTaskInput extends Input<FileTask> implements HasId {
    Class<FileTask> mappingTarget = FileTask.class;
    Long id;
    boolean current;
    FileActivityInput fileActivity;
    TaskInput task;
    LocalDateTime toStartDate;
    LocalDateTime dueDate;
    LocalDateTime startDate;
    LocalDateTime endDate;
    AgentInput reporter;
    AgentInput assignedTo;
    List<FileTaskSituationInput> fileTaskSituations;
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
