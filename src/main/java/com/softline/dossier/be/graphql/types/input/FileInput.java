package com.softline.dossier.be.graphql.types.input;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@SuperBuilder

@Data
@NoArgsConstructor
public class FileInput
{

    String project;
    LocalDate attributionDate;
    LocalDate returnDeadline;
    LocalDate provisionalDeliveryDate;
    LocalDate deliveryDate;
    ClientInput client;
    CommuneInput commune;
    List<FileDocInput> fileDocs;
    List<FileStateInput> fileStates;
    List<FileActivityInput> fileActivities;
    ActivityInput baseActivity;
    boolean fileReprise;
    FileStateInput currentFileState;
    FileActivityInput currentFileActivity;
    FileInput reprise;
    boolean inTrash;
    private Long id;
    long order;
}
