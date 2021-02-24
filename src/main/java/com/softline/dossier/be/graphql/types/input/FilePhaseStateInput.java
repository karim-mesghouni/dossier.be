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
public class FilePhaseStateInput {

    long id;
    boolean current;
    List<FilePhaseAgentInput> phaseAgents;

    PhaseStateInput state;
    List<BlockingInput> blockings;
}
