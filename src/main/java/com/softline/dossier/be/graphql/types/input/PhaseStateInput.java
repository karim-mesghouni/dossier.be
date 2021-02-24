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
public class PhaseStateInput  {

    long id;
    String state;
    boolean initial;
    boolean Final;

    PhaseInput phase;

    List<FilePhaseStateInput> filePhaseStates;
}
