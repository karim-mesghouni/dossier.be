package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FilePhaseAgentInput {

    long id;
    Date toStartDate;
    Date dueDate;
    Date startDate;
    Date endDate;
    boolean current;


    FilePhaseInput filePhase;
    AgentInput reporter;

    AgentInput assignedTo;

    FilePhaseStateInput filePhaseState;
}
