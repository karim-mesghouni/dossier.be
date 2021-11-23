package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Concerns.HasId;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class FileInput implements HasId {
    String project;
    LocalDate attributionDate;
    LocalDate returnDeadline;
    LocalDate provisionalDeliveryDate;
    LocalDate deliveryDate;
    ClientInput client;
    CommuneInput commune;
    List<FileStateInput> fileStates;
    List<FileActivityInput> fileActivities;
    ActivityInput baseActivity;
    boolean fileReprise;
    FileStateInput currentFileState;
    FileActivityInput currentFileActivity;
    FileInput reprise;
    boolean inTrash;
    long order;
    long id;
    AgentInput agent;
}
