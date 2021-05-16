package com.softline.dossier.be.graphql.types.input;

import com.softline.dossier.be.domain.Activity;
import com.softline.dossier.be.domain.FileActivity;
import com.softline.dossier.be.domain.FileState;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Transient;
import java.io.File;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@SuperBuilder

@Data
@NoArgsConstructor
public class FileInput {

    private Long id;

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

    FileStateInput currentFileState;
    FileActivityInput currentFileActivity;
    FileInput reprise;

}
